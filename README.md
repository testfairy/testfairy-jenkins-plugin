[![Build Status](https://travis-ci.org/testfairy/testfairy-jenkins-plugin.svg?branch=master)](https://travis-ci.org/testfairy/testfairy-jenkins-plugin)
# Testfairy-jenkins-plugin
This plugin integrates TestFairy platform with the Jenkins build system.
For more details and instruction check out [https://wiki.jenkins-ci.org/display/JENKINS/TestFairy+Plugin](https://wiki.jenkins-ci.org/display/JENKINS/TestFairy+Plugin)

# Build

```bash
# Build
docker run -it --rm -v `pwd`:`pwd` -w `pwd` --env TF_API_KEY=<yourkey> androidsdk/android-30:latest bash scripts/build.sh

# Use built binary
ls -a target/TestFairy.hpi
```

# Develop

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

## How to add a new feature?

1. Create a recorder (example: `src/main/java/org/jenkinsci/plugins/testfairy/TestFairyIosRecorder.java`)
2. Create a UI resource for the recorder (example: `src/main/resources/org/jenkinsci/plugins/testfairy/TestFairyIosRecorder/**/*.jelly`)
3. Create a new test (example: `scripts/upload_test/UploadTest.xml`)
4. Set it up (example: `scripts/upload_test/upload_test.sh`)
5. Add it to the suite (example: `scripts/test_jenkings_plugin.sh`)

# Publish

1. Merge a PR to master and create a release. (ad-hoc Jenkins)
2. Merge this repo to https://github.com/jenkinsci/testfairy-plugin origin. (public Jenkins)
3. Create a release in https://github.com/jenkinsci/testfairy-plugin repo.


