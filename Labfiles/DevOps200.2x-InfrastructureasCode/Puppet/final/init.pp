class mrpapp {
  class { 'configuremongodb': }
  class { 'configurejava': }
  class { 'configuretomcat': }
  class { 'deploywar': }
  class { 'orderingservice': }
}

class configuremongodb {
  include wget
  class { 'mongodb': }->

  wget::fetch { 'mongorecords':
    source => 'https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/deploy/MongoRecords.js',
    destination => '/tmp/MongoRecords.js',
    timeout => 0,
  }->
  exec { 'insertrecords':
    command => 'mongo ordering /tmp/MongoRecords.js',
    path => '/usr/bin:/usr/sbin',
    unless => 'test -f /tmp/initcomplete'
  }->
  file { '/tmp/initcomplete':
    ensure => 'present',
  }
}

class configurejava {
  include apt
  $packages = ['openjdk-8-jdk', 'openjdk-8-jre']

  apt::ppa { 'ppa:openjdk-r/ppa': }->
  package { $packages:
     ensure => 'installed',
  }
}

class configuretomcat {
  class { 'tomcat': }

  tomcat::install { 'var/lib/tomcat7':
        catalina_home => '/var/lib/tomcat7',
        source_url => 'http://www.apache.org/dist/tomcat/tomcat-7/v7.0.88/bin/apache-tomcat-7.0.88.tar.gz',
  }->
  tomcat::config::server::connector { 'tomcat7-http':
    catalina_base => '/var/lib/tomcat7',
    port => '9080',
    protocol => 'HTTP/1.1',
    connector_ensure => 'present',
   server_config => '$(Catalina_base)/config/server.xml',

  }->
   tomcat::service { 'default':
        catalina_base => '/var/lib/tomcat7',
        service_ensure => 'running',
        use_jsvc => false,
        use_init => false,
        service_name => 'tomcat',
  }
}

class deploywar {
  require configuretomcat

  tomcat::war { 'mrp.war':
    catalina_base => '/var/lib/tomcat7',
    war_source => 'https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/builds/mrp.war',
  }
}

class orderingservice {
  package { 'openjdk-7-jre':
    ensure => 'installed',
  }

  file { '/opt/mrp':
    ensure => 'directory'
  }->
  wget::fetch { 'orderingsvc':
    source => 'https://raw.githubusercontent.com/Microsoft/PartsUnlimitedMRP/master/builds/ordering-service-0.1.0.jar',
    destination => '/opt/mrp/ordering-service.jar',
    cache_dir => '/var/cache/wget',
    timeout => 0,
  }->
  exec { 'stoporderingservice':
    command => "pkill -f ordering-service",
    path => '/bin:/usr/bin:/usr/sbin',
    onlyif => "pgrep -f ordering-service"
  }->
  exec { 'stoptomcat':
    command => 'service tomcat7 stop',
    path => '/usr/bin:/usr/sbin',
    onlyif => "test -f /etc/init.d/tomcat7",
  }->
  exec { 'orderservice':
    command => 'java -jar /opt/mrp/ordering-service.jar >> /tmp/log.txt &',
    path => '/usr/bin:/usr/sbin:/usr/lib/jvm/java-8-openjdk-amd64/bin',
  }->
  exec { 'wait':
    command => 'sleep 20',
    path => '/bin',
    notify => Tomcat::Service['default']
  }
}