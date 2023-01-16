source "https://rubygems.org"

gem "fastlane"
gem "openssl"
gem "base64"
gem "aws-sdk"
gem "json"
gem "git"

plugins_path = File.join(File.dirname(__FILE__), 'fastlane', 'Pluginfile')
eval_gemfile(plugins_path) if File.exist?(plugins_path)
