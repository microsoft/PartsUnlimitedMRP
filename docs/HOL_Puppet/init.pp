class mrpapp {
  class { 'configuremongodb': }->
  class { 'deploywar': }->
  class { 'orderingservice': }->
  class { 'configuretomcat': }
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

class configuretomcat {
  class { 'tomcat': }

  tomcat::instance { 'default':
    package_name => 'tomcat7',
    install_from_source => false,
  }
  tomcat::config::server::connector { 'tomcat7-http':
    catalina_base => '/var/lib/tomcat7',
    port => '9080',
    protocol => 'HTTP/1.1',
    connector_ensure => 'present',
    server_config => '/etc/tomcat7/server.xml',
  }
  tomcat::service { 'default':
    use_jsvc => false,
    use_init => true,
    service_name => 'tomcat7',
  }
}

class deploywar {
  file { '/var/lib/tomcat7/webapps':
      ensure => 'directory',
  }
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
  exec { 'orderservice':
    command => 'java -jar /opt/mrp/ordering-service.jar &',
    path => '/usr/bin:/usr/sbin:/usr/lib/jvm/java-8-openjdk-amd64/bin',
  }->
  exec { 'wait':
    command => 'sleep 30',
    path => '/bin',
  }
}
