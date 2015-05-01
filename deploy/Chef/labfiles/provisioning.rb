require 'chef/provisioning/azure_driver'

with_chef_server "https://dm-chefserver.cloudapp.net/organizations/fabrikam",
:client_name => Chef::Config[:node_name],
:signing_key_filename => Chef::Config[:client_key]
with_driver 'azure'

machine_options = {
 :bootstrap_options => {
 :cloud_service_name => 'chefunicorncs',
 :storage_account_name => 'chefunicornsa',
 :vm_size => "Small",
 :location => 'East US',
 :tcp_endpoints => '9080:9080,8080:8080'

 },
 :image_id => 'b39f27a8b8c64d52b05eac6a62ebad85__Ubuntu-14_04_2_LTS-amd64-server-20150309-en-us-30GB',
 # Until SSH keys are supported (soon)
 :password => "P2ssw0rd",
 :convergence_options => { ssl_verify_mode: :verify_none }

}

machine 'chefunicorn' do
 machine_options machine_options
 role 'mrp'
end
