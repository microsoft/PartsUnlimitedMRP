class mrpapp {
  class { 'configuremongodb': }
  class { 'configurejava': }
  class { 'creategroup': }
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


class creategroup {

group { 'tomcat':
  ensure => 'present',
  gid    => '10004',
  }

user { 'tomcat':
  ensure           => 'present',
  gid              => '10003',
  home             => '/tomcat',
  password         => '!',
  password_max_age => '99999',
  password_min_age => '0',
  uid              => '10003',
  }

}



class configuretomcat {
  class { 'tomcat': }
 require creategroup


 tomcat::instance { 'default':
  catalina_home => '/var/lib/tomcat7',
  install_from_source => false,
  package_name => 'tomcat7',

 }->

 tomcat::config::server::connector { 'tomcat7-http':
  catalina_base => '/var/lib/tomcat7',
  port => '9080',
  protocol => 'HTTP/1.1',
  connector_ensure => 'present',
  server_config => '/etc/tomcat7/server.xml',

 }->

 tomcat::service { 'default':
  use_jsvc => false,
  use_init => true,
  service_name => 'tomcat7',

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
    path => '/bin:/usr/bin:/usr/sbin',
    onlyif => "test -f /etc/init.d/tomcat7",
  }->
  exec { 'orderservice':
    command => 'java -jar /opt/mrp/ordering-service.jar &',
    path => '/usr/bin:/usr/sbin:/usr/lib/jvm/java-8-openjdk-amd64/bin',
  }->
  exec { 'wait':
    command => 'sleep 20',
    path => '/bin',
    notify => Tomcat::Service['default']
  }
}






























class configuretomcat {
	class { '::tomcat': }

	tomcat::install { '/var/lib/tomcat':
  	source_url => 'http://www.apache.org/dist/tomcat/tomcat-7/v7.0.88/bin/apache-tomcat-7.0.88.tar.gz',
	}
	tomcat::instance { 'default':
 	 catalina_home => '/var/lib/tomcat7',
  	 catalina_base => '/var/lib/tomcat7',
 	 install_from_source => false,
 	 package_name        => 'tomcat7',

	}
	->
  	tomcat::config::server::connector { 'tomcat7-http':
    	 catalina_base => '/var/lib/tomcat7',
  	 port => '9080',
  	 protocol => 'HTTP/1.1',
  	 connector_ensure => 'present',
  	 server_config => '$(Catalina_base)/conf/server.xml',


	}
	->
  	tomcat::config::server::connector { 'tomcat7-http':
    	 catalina_base => '/var/lib/tomcat7',
  	 port => '9080',
  	 protocol => 'HTTP/1.1',
  	 connector_ensure => 'present',
  	 server_config => '$(Catalina_base)/conf/server.xml',

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
    path => '/bin:/usr/bin:/usr/sbin',
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


********************************************************************************************************

>>>>>>>>>this works needed to create user tomcat and group tomcat on node ubuntu VM>>> 13th June 2018>>



class configuretomcat {
  class { 'tomcat': }

        tomcat::instance { 'default':
                catalina_home => '/var/lib/tomcat7',
                catalina_base => '/var/lib/tomcat7',
                package_name => 'tomcat7',
                install_from_source => false,
#               user => azureuser
                }
tomcat::config::server::tomcat_users {
 'tomcat-admin':
    catalina_base => '/var/lib/tomcat7',
    element  => 'user',
    password => 'test',
    roles => ['manager-gui','admin'];
 'deployer':
    catalina_base => '/var/lib/tomcat7',
    element => 'user',
    password => 'deployer',
    roles => ['manager-script'];

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

