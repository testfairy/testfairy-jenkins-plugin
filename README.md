# Jenkins : TestFairy Plugin

This plugin helps you to upload Android APKs or iOS IPA files to [www.testfairy.com](http://www.testfairy.com)

Older versions of this plugin may not be safe to use. Please review the following warnings before using an older version:

- [Credentials stored in plain text](https://jenkins.io/security/advisory/2019-04-03/#SECURITY-1062)

## How to use

Select **TestFairy iOS/Android** **Uploader** from **Add post-build action** menu

![](docs/dropdown.png)

**Job Configuration:**

![](docs/job-config.png)
- The API Key can be found in [your account settings](https://app.testfairy.com/settings/)

To make sure Jenkins also uploads your iOS dSYMs to TestFairy, configure the Xcode that builds the app on the Jenkins server to run a special script. [Follow these instructions](https://docs.testfairy.com/iOS_SDK/How_To_Upload_dSYM.html#upload-dsym-from-xcode).

The resulted TestFairy links will be listed in the console output

![](docs/check-success.png)

##### **Important** **f****or** **Advanced Uploader only:**

* You should configure the "Path To Keystore file" and the corresponding storepass & alias.
* You should configure "**TestFairy Android Environment"** on [http://localhost:8080/configure](http://localhost:8080/configure) for example: ![](docs/jarsigner-config.png)

## Custom changelog

In order to add your own changelog or comments, please create a text file in the following location:

`$JENKINS_HOME/jobs/$JOB_NAME/builds/$BUILD_ID/testfairy_change_log`

The content of this file will override the default changelog.

## Private Cloud / On-Premise support

In case you are using our Private Cloud or On-Premise product

please define an `TESTFAIRY_UPLOADER_SERVER` environment variable.

You can do it by going to **Manage Jenkins** -> **Configure**

Under section Global Properties, check **Environment variables checkbox**. Now Jenkins allow us to add key and value pairs.

The name should be `TESTFAIRY_UPLOADER_SERVER` and the value will be your **server domain.**

![](docs/env.png)

The following line should be printed at your next job console output:

`The server will be https://YOUR-PRIVATE-SERVER-SUBDOMAIN.testfairy.com/`

# Development Guide

## Build

```bash
# Build
docker run -it --rm -v `pwd`:`pwd` -w `pwd` --env TF_API_KEY=<yourkey> androidsdk/android-30:latest bash scripts/build.sh

# Use built binary
ls -a target/TestFairy.hpi
```

## Develop

```bash
# Launch development environment
docker run -it --rm -v `pwd`:`pwd` -w `pwd` --env TF_API_KEY=<yourkey> androidsdk/android-30:latest bash

# inside docker
  # Attempt first build and prepare environment
  bash scripts/build.sh
  
  # Develop new features
  
  # Run tests after code change
  bash scripts/retry.sh

  # Quit docker and REMOVE environment while keeping the changes in source code
  exit

# Push
git ...
```

### How to add a new feature?

1. Create a recorder (example: `src/main/java/org/jenkinsci/plugins/testfairy/TestFairyIosRecorder.java`)
2. Create a UI resource for the recorder (example: `src/main/resources/org/jenkinsci/plugins/testfairy/TestFairyIosRecorder/**/*.jelly`)
3. Create a new test (example: `scripts/upload_test/UploadTest.xml`)
4. Set it up (example: `scripts/upload_test/upload_test.sh`)
5. Add it to the suite (example: `scripts/test_jenkings_plugin.sh`)

## Publish

1. Merge a PR to master.
2. Merge this repo to [origin](https://github.com/jenkinsci/testfairy-plugin/compare/master...testfairy:master).
3. [Trigger](https://github.com/jenkinsci/testfairy-plugin/actions/workflows/publish-artifactory.yml) `Publish to Artifactory` workflow.
4. Check release [here](https://get.jenkins.io/plugins/TestFairy/) once CI completes. (sometimes it takes a few extra minutes)