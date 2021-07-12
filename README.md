# Smile_ID_Android_Sample

The official Smile Identity Sample Application is a Java-based Android application that illustrates how to integrate with Smile Identity's Android library. It leverages all major features, covering different job submission scenarios.

## Deployment

The Android Sample Application is deployed using the mobile CI/CD tool - [Bitrise](https://www.bitrise.io/), either directly from the application's Bitrise portal or using a set of custom Slack commands.

#### Bitrise Portal

#### Slack Command

A build can be triggered from Slack using the following command `/deploy branch: [branch_name] | workflow: [workflow_name]` where `[branch_name]` is the name of the branch hosting the latest changes to deploy from and `[workflow_name]` one of the existing two workflows - `test` or `deploy`.

For instance, the following command `/deploy branch: feat/user_consent | workflow: deploy` deploys the changes committed to the `feat/user_consent` branch into an Android build - either `.apk` file or a bundle `.aab`.

Once the process is completed, the link to the generated build will be made available on the Slack `#notifications`. Alternatively, an email containing more detailed information related to the build will be sent to all user groups attached to the related Bitrise application. More information on how to register as Developer or a Tester on Bitrise can be found [here](https://devcenter.bitrise.io/team-management/adding-new-team-member/); however, this can only be done by invitation from the Bitrise application's owner or any other user with admin privileges.

## Contributing

Bug reports and pull requests are welcome on GitHub at https://github.com/smileidentity/Smile_ID_Android_Sample
