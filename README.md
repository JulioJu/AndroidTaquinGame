# TP Développement Web Mobile : Création d'un jeu de taquin

## Table of Contents

<!-- vim-markdown-toc GFM -->

* [Teacher's instruction and JulioJu functionalities](#teachers-instruction-and-julioju-functionalities)
    * [Consignes](#consignes)
* [Fonctionnalités](#fonctionnalités)
    * [Les plus :](#les-plus-)
    * [Packaging](#packaging)
    * [Architecture/code](#architecturecode)
* [Notes from JulioJu](#notes-from-julioju)
    * [Genymotion and IntelliJ](#genymotion-and-intellij)
        * [IntelliJ vs Android Studio](#intellij-vs-android-studio)
        * [Editorconfig file](#editorconfig-file)
        * [NeoVim and the Language Server Protocol (based on Eclipse)](#neovim-and-the-language-server-protocol-based-on-eclipse)
    * [Git history](#git-history)
    * [Android Source Code and documentation](#android-source-code-and-documentation)
    * [Examples downloaded at Google Samples](#examples-downloaded-at-google-samples)
    * [AndroidManifest.xml](#androidmanifestxml)
        * [`<intent-filter>`](#intent-filter)
    * [Constraint Layout and Layout docs](#constraint-layout-and-layout-docs)
    * [Tasks and back-stack](#tasks-and-back-stack)
    * [Intents](#intents)
        * [Android Permissions](#android-permissions)
        * [Work with images and files](#work-with-images-and-files)
    * [MediaScannerConnection / Intent.ACTION_MEDIA_SCANNER_SCAN_FILE](#mediascannerconnection--intentaction_media_scanner_scan_file)
    * [Class diagram](#class-diagram)
    * [Notes misc](#notes-misc)
* [My implementation](#my-implementation)
    * [./app/src/main/java/fr/uga/julioju/taquingame/taquin/TaquinActivity.java](#appsrcmainjavafrugajuliojutaquingametaquintaquinactivityjava)
    * [Square board prototype](#square-board-prototype)
* [Tested](#tested)
* [TODO](#todo)

<!-- vim-markdown-toc -->

## Teacher's instruction and JulioJu functionalities
Développement Web mobile
2017-2018
Android – Projet Taquin

Sources:
http://imss-www.upmf-grenoble.fr/~davidjer/dwm/Projet-Taquin2017.pdf
### Consignes
## Fonctionnalités
* On peut choisir soit une image de la galerie du téléphone, soit prendre
    une photo.
    * Notes from JulioJu:
        * on my implementation try to take a photo saved
            in Pubic Directory ask a permission even if the permission was
            forbidden in `settings -> App Info -> applicationName -> permission`
            and if a user has denied a permission and selected the `Don't ask
            again` option in the permission request dialog,
        * As needed in API 26, permission is asked the first time
            we try to take a photo and save it in a Public Repository (
            no one Runtime Permission seems to be needed when the photo is
            saved in the Private Repository.
        * When a not displayable picture is selected, the game should work
            anyway.
* On peut configurer la taille de la grille (2x2, 3x3, 4x4).
    * Note from JulioJu: except for 2X2, positions should be random
* L'image doit être bien orientée (mode paysage ou portrait,
    d'après infos EXIF).
* L'image doit être adaptée à la taille et résolution de l'écran (pas d'image
    trop pixelisée ou trop grande)
    * Note from JulioJu:
        * Bitmap should be loaded efficiently
            https://developer.android.com/topic/performance/graphics/load-bitmap
        * Ratio `Screen / photo < 5`
* Lorsque le jeux est gagné, un message doit être affiché puis une option pour
recommencer ou quitter l'appli doit être proposée.

### Les plus :
* une animation lors du déplacement d'un bout d'image
    http://developer.android.com/reference/android/view/animation/TranslateAnimation.html
    * Note by JulioJu. When there is the animation, mouse click
        should done nothing.
* faire tourner l'application quand on tourne le téléphone.
    * Note by JulioJu: without loose the state.

### Packaging
* L'appli doit être déployable et livrée sous forme d'un APK.

### Architecture/code
* Les appel à la galerie et appareil photo seront fait par intent.
* Le code doit être lisible et modulaire
* Une démo sera à effectuer le jour de la soutenance des deux projets Android.
* Un court document expliquant comment chacun des points suivant à été réalisé
    devra être fourni.

## Notes from JulioJu
* The first commit could not compile. We must have at least one activity !

### Genymotion and IntelliJ
* On my computer, Genymotion is very very faster and lighter (for the CPU) than
    the Android Emulator. I've also very heard than it's very more reliable.
* It's free. And with a registration, you could have lot of different
    Virtual Images.
* **For Intellij Ultimate Edition, you must configure the debugger. See:
    https://stackoverflow.com/a/51476111**
    Read also the comment of this comment !
* Do not use the file explorer `Amaze`. Little bit buggy (don't recognize
    some root folders). Use `ES File Explorer`.
    Install it simply by download it, then drag and drop to the VM screen.
* On my Arch Linux, when Genymotion is launched, can't hear noise from the
    computer.  E.g. when I start a new `vlc` or `Youtube.com` (with Chrome or
    Firefox), or even the sound manager can't hear noise from it.  Simply stop
    the VM then `vlc` or Youtube video works, then start again the VM and all
    continue to work fine! Then, you could also restart `vlc` or `youtube` and
    all continue to work! Probably, Genymotion take a lock if it can.
* When import, we must build to see some errors disappear.
* I've experienced errors with `Google AVD` (Android Emulator)
    * https://stackoverflow.com/questions/38450717/session-app-error-while-installing-apk
    Not read all the comments. I've only experienced to disable `Instant run`
    (no try others complex solutions).
    But Instant Run (`Tools --> Apply changes`) is very useful ! Therefore
    kicked off `Google Emulator`.
    * On my Computer, when `AVD` loose the focus, his screen becomes black
        even when he has the focus again. The only workaround
        is to change orientation (`e.g.` Portrait -> Landscape)

#### IntelliJ vs Android Studio
* On my Arch Linux, with `IntelliJ` I've tried to use `Settings -> Android SDK`
    to install SDK, but without successes. I've tried many times. It works
    well with `Android Studio`.

* In August, the last `IntelliJ` generate a little bit older template than
    `Android Studio`. I've seen than thanks the version of Gradle generated.
    To generate a project with a template, go to `File -> -> New -> New Project`

#### Editorconfig file
* The code have line with `max_line_length = 80`.
* As Jet Brains could show parameter name hint, this barrier could be crossed.
    But it's a false positive.

#### NeoVim and the Language Server Protocol (based on Eclipse)

Maybe investigate following for other projects:

* See https://github.com/neoclide/coc-java
* See https://github.com/redhat-developer/vscode-java/issues/10#issuecomment-446508968
    (for VSCode, but should work for NeoVim)



### Git history
* For a newbie, the three firsts commits are interesting.

### Android Source Code and documentation
* Android
    https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android

* Constraint Layout
    https://android.googlesource.com/platform/frameworks/opt/sherpa/+/studio-3.1.2
    (replace `studio-3.1.2` by the release used)

* View sources in Android-studio / IntelliJ:
    https://stackoverflow.com/questions/21221679/android-studio-how-to-attach-android-sdk-sources
    appcompat is also viewable by this method. Check if versions are correctly set.

* View sources for other dependencies (e.g.) we could download and attach it
    manually in IntelliJ. Maybe there is other easier solution.
    For ConstraintLayout,
    1. `git clone https://android.googlesource.com/platform/frameworks/opt/sherpa`
    2. `git checkout studio-3.1.2`
    3.  then two solutions:
        * `import android.support.constraint.Barrier` in a Java file,
    then `Right click -> Go To -> Declaration`, then click in the Warning pakne
        * Or https://www.jetbrains.com/help/idea/project-library-and-global-library-pages.html

* constraint-layout-solver:1.1.2
    * For `com.android.support.constraint/constraint-layout-solver/1.1.2`,
        "`Library source does not match the bytecode for class Barrier`" (message
        from IntelliJ).
    * I've downloaded source from:
        https://android.googlesource.com/platform/frameworks/opt/sherpa/+/studio-3.1.2.

* `AppCompatActivity`. We could read more complete documentation at
    https://developer.android.com/reference/android/app/Activity, even if is
    not the same class. AppCompatActivity is added in version 25.1.0. Maybe
    **do not compile for version lower than 25.1.0**. TODO confirm that.

* Input and Events: https://developer.android.com/guide/topics/ui/ui-events

* ***When you open source in Intellij or Android Studio***, activate `File ->
    Power Save Mode` otherwise Intellij analyze the file opened (could take a
    long time even if on an I5 7th gen).

### Examples downloaded at Google Samples
* An easy solution to test examples at https://github.com/googlesamples/
    without compatibilities errors (build-tools, platforms, gradle, etc.)
    is simply :
    1. create an empty project in the IDE.
    2. `rm -R project_name/app/src/main/*`
    3. `cp -R google_sample/Application/src/main/* projectname/app/src/main`

* Tool https://developer.android.com/samples/ is very cool to search samples.

* Actually (09/2018), in documentation, links to samples are broken.

### AndroidManifest.xml

To understand ./app/src/main/AndroidManifest.xml, following resources are
interesting:

https://developer.android.com/guide/topics/manifest/manifest-intro
https://developer.android.com/guide/components/activities/intro-activities

#### `<intent-filter>`
https://developer.android.com/guide/topics/manifest/intent-filter-element

https://developer.android.com/guide/topics/manifest/action-element
    « *The name of the action. Some standard actions are defined in the Intent
  class as ACTION_string constants.* »

https://developer.android.com/guide/topics/manifest/category-element
    « *The name of the action. Some standard actions are defined in the Intent
  class as ACTION_string constants.* »

https://developer.android.com/reference/android/content/Intent

### Constraint Layout and Layout docs
* **./app/src/main/java/fr/uga/julioju/taquingame/taquin/Square.java**
    could be understood thanks this comments.

* How I don't use `Grid Layout` as asked by the teacher, because:
    « *Note: For better performance and tooling support, you should instead build
    your layout with ConstraintLayout.* » (read
    https://developer.android.com/guide/topics/ui/layout/gridview)

* ~~Thanks `android.support.constraint.Barrier` appeared since
        `Constraint Layout 1.1.0 beta 1` (see [this release note](https://androidstudio.googleblog.com/2017/05/constraintlayout-110-beta-1-release.html)), we could easy draw a Grid.
        `Constraint Layout 1.1.0` is available since Thursday, April 12, 2018 (see
        [this release not](https://androidstudio.googleblog.com/2018/04/constraintlayout-110.html))
        **Ref:**  https://stackoverflow.com/questions/42846261/trying&#45;to&#45;replicate&#45;gridlayout&#45;column&#45;alignment&#45;with&#45;constraintlayout~~
    * Implemented without `Barrier`!! Not useful. Only ConstraintSet between
        Squares, as Square have fix width and high ! Vertical `Barrier` are
        interesting only if TextWidth have not fix width !
        The example found in Stackoverflow was very bad.


* Official guide than explain what is a Layout (very interesting)
    https://developer.android.com/guide/topics/ui/declaring-layout

* ConstraintLayout vs RelativeLayout
    https://stackoverflow.com/a/46330178

* Constraint Layout API Reference
    https://developer.android.com/reference/android/support/constraint/ConstraintLayout

* To understand attribute `android:orientation`
    https://developer.android.com/reference/android/widget/LinearLayout

* To understand attribute `android:layout_weight`
    https://developer.android.com/guide/topics/ui/layout/linear

* To understand android.support.constraint.ConstraintLayout
    https://developer.android.com/reference/android/support/constraint/Barrier

* In Constraint Layout: Barrier vs Guideline:
    https://stackoverflow.com/questions/47114672/what&#45;is&#45;difference&#45;between&#45;barrier&#45;and&#45;guideline&#45;in&#45;constraint&#45;layout
    « *The only difference between Barrier and Guideline is that Barrier's
    position is flexible and always based on the size of multiple UI elements
    contained within it and Guideline's position is always fixed.* »

* **Source Code**:
    https://android.googlesource.com/platform/frameworks/opt/sherpa/+/studio-3.1.2

* To understand `START`, `END`, `BOTTOM`, `LEFT`, `RIGHT`, `START` constants, read
    https://developer.android.com/reference/android/support/constraint/ConstraintLayout.LayoutParams

* To understand `WRAP_CONTENT` and `MATCH_PARENT` constants, read
    https://developer.android.com/reference/android/support/constraint/ConstraintLayout.LayoutParams

* To understand constructor `ConstraintLayout.LayoutParams(int, int)`, read
    source code.

* Immersive mode, read:
    * https://developer.android.com/training/system-ui/immersive
    * https://github.com/googlesamples/android-ImmersiveMode/blob/master/Application/src/main/java/com/example/android/immersivemode/ImmersiveModeFragment.java
    * ***Do not compile for version < 18*** Otherwise, add `if` to remove not
        compatibles flags
    * Navigation bar seems not be used. Therefore Immersive Mode seems not te be
    * interesting
    * Do not forget to place the code on `onResume()`.

* No title, no action bar
    https://stackoverflow.com/questions/26878386/androidwindownotitle-will-not-hide-actionbar-with-appcompat-v7-21-0-0

* Background.
    * To have **very cool backgrounds** there is : http://angrytools.com/gradient/
    (Found at https://stackoverflow.com/a/13930148).
    * This resource is also interesting
        https://stackoverflow.com/questions/3496269/how-do-i-put-a-border-around-an-android-textview

* Size of screen and dpi and dip
    * `dp` unit is used
        1. to determine the screen size and
            https://developer.android.com/training/multiscreen/screensizes
            * Screen could be for exemple `small`(<600 dp),
                `normal` (>= 600 dp)(7” tablets),
                `medium` (>=960dp)(10” tablets), `large` (>=1280dp),
                and `xlarge` (>= 1920dp)
            * On my Lenovo T470 Full HD, "Custon Phone 768X1280"
                with 320 dpi is a `sw384dp`.
            * On my Lenovo T470 Full HD, "Custon Tablet 1536X2048"
                with 320 dpi is a `sw768dp`.
            * Some references:
                * 320dp: a typical phone screen (240x320 ldpi, 320x480 mdpi,
                    480x800 hdpi, etc).
                * 480dp: a large phone screen ~5" (480x800 mdpi).
                * 600dp: a 7” tablet (600x1024 mdpi).
                * 720dp: a 10” tablet (720x1280 mdpi, 800x1280 mdpi, etc).
        2. to have objects (font or drawable) that have the same length in
            meters, it's independent of `dpi` of the screen.
            https://developer.android.com/training/multiscreen/screendensities
    * When defining text sizes, however, you should instead use scalable pixels
        (sp) as your units (but never use sp for layout sizes). The sp unit is
        the same size as dp, by default, but it resizes based on the user's
        preferred text size.
        https://developer.android.com/training/multiscreen/screendensities
    * Remember than `px = dp * (dpi / 160)` (it's a **division** !!!!!)
    * https://stackoverflow.com/questions/24579608/android-resource-qualifiers-swdp-vs-wdp
            * smallestWidth - sw<N>dp - The smallestWidth is a fixed screen size
            characteristic of the device; the device's smallestWidth does not
            change when the screen's orientation changes.
            * Available width - w<N>dp - This configuration value will change when
            the orientation changes between landscape and portrait to match the
            current actual width.
    * https://developer.android.com/training/multiscreen/screensizes
        ```
        res/layout/main_activity.xml                # For handsets
        res/layout-land/main_activity.xml           # For handsets in landscape
        res/layout-sw600dp/main_activity.xml        # For 7” tablets
        res/layout-sw600dp-land/main_activity.xml   # For 7” tablets in landscape
        ```
    * https://developer.android.com/training/multiscreen/screendensities
        "To provide good graphical qualities on devices with different pixel densities, you should provide multiple versions of each bitmap in your app".
        There is:
        `ldpi`, `mdpi`, `hdpi`, `xhdpi`, `xxhdpi`, `xxxhdpi`, `nodpi`, `tvdpi`
    * How detect screen size?
        1. https://stackoverflow.com/questions/9279111/determine-if-the-device-is-a-smartphone-or-tablet
        2. https://stackoverflow.com/questions/5832368/tablet-or-phone-android
        3. **Solution that I like** :
        https://stackoverflow.com/questions/15055458/detect-7-inch-and-10-inch-tablet-programmatically/15133776#15133776
        * **Warning**:
            « The returned size may be adjusted to exclude certain system decor
            elements that are always visible ». For example, Navigation bar !
            https://developer.android.com/reference/android/view/Display
    * To see statistics: https://developer.android.com/about/dashboards/

* For Square.java:
    * « match_parent is not supported. With 0dp, you can think of your
        constraints as 'scalable' rather than 'filling whats left'. »
        https://stackoverflow.com/questions/37603751/set-width-to-match-constraints-in-constraintlayout
    * As squareWidth is the result of an Integer division, it could be have
        space at the right of the screen and at the bottom of the screen.
        To correct this, I've added a `margin_left` to Square on the
        first column, and a `margin_top` to Square on the first row. I've
        also added a background for the activity, to not have white background.

* ConstraintSet should be set after layout.addView(view);

* When we display size resolution in a Toast at the launch of
    TaquinActivity, the `hight` is lower than the real height.
    It's because:
    « The returned size may be adjusted to exclude certain system decor
    elements that are always visible ». For example, Navigation bar !
    https://developer.android.com/reference/android/view/Display

### Tasks and back-stack
* Read https://developer.android.com/guide/components/activities/tasks-and-back-stack#Clearing
* In AndroidManifest.xml, don't forget to declare `alwaysRetainTaskState`
    in the root activity.
* We could not use an Intent to `Intent.ACTION_MAIN`, with flag
    `Intent.FLAG_ACTIVITY_CLEAR_TOP` like described in
    https://stackoverflow.com/a/22960764. This solution doesn't terminate
    the app, doesn't remove from `Recent Screen`.
* The solution that I've found: to launch an new Intent,
    use `Activity.startActivityForResult`. Then, create a new object that say if
    in the last Activity of the task, we have clicked on « Stop app » or « play
    again app ». If we have clicked on « Stop app », in `onActivityResult()`
    call `Activity.finishAndRemoveTask`.  `Activity.finishAndRemoveTask` is cool
    because it delete task from "`Recents Screen`".
* Be careful, "`Recents Screen`" doesn't show  Tasks, but shows Screen.
    https://developer.android.com/guide/components/activities/recents
* `Activity.finishAndRemoveTask()` doesn't finish parent tasks.
* `onDestroy()`
    « The final call you receive before your activity is destroyed. This can happen
    either because the activity is finishing (someone called finish() on it, or
    because the system is temporarily destroying this instance of the activity
    to save space. »
    https://developer.android.com/reference/android/app/Activity
* Therefore, « back » button call « onDestroy ».
* Therefore to launch MainActivity.java from TaquinActivity.java
    call `finish()`, and not trigger `startActivity`, otherwise
    a new instance of `MainActivity` will be put on the top of the `task`.

### Intents
* Introduction to intents:
    https://developer.android.com/training/sharing/
* Generality about intents:
    https://developer.android.com/guide/components/intents-filters
* Getting a Result from an Activity
    https://developer.android.com/training/basics/intents/result
* Forward intents:
    * Third activity called returns its result to the first activity
        Use `intentOutcome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)`.
        To well understand this flag:
        https://gist.github.com/mcelotti/cc1fc8b8bc1224c2f145
    * To forward Intent parameter through chains of Activities:
        Use: `intentOutcome.putExtras(super.getIntent());`
        https://stackoverflow.com/a/12905952
* Read also the example in
    ./app/src/main/java/fr/uga/julioju/taquingame/main/MainActivity.java
* Be careful: content of an Intent should be `< 1 M` otherwise raise:
    https://developer.android.com/reference/android/os/TransactionTooLargeException.
    I've experimented that when I've sent through an Intent a Bitmap too large.
    Confirmed by the article:
    https://developer.android.com/guide/components/activities/parcelables-and-bundles
    * It seems not have very good solution for this problem.
        Only solutions to send data from one activity to another
        seems to use persistence (database, files) or Intent. I've searched
        a lot.
        Maybe on Android API `< 23` there is not this problem.
        * https://stackoverflow.com/questions/11451393/what-to-do-on-transactiontoolargeexception
        * https://github.com/livefront/bridge/issues/19
        * https://medium.com/@mdmasudparvez/android-os-transactiontoolargeexception-on-nougat-solved-3b6e30597345
    * ***Therefore*** do not use Intent to transfer bit datas

* Be careful Parcelable[] could not be cast to Bitmap[]:
    `Caused by: java.lang.ClassCastException: android.os.Parcelable[] cannot be cast to android.graphics.Bitmap[]`
    Solution:
    use `ArrayList<Bitmap>` and use `intentIncome.getParcelableArrayListExtra()`

* Use a `Intent.createChooser` is a good practice because
    « If no applications match, Android displays a system message. »
* In the particular case of ACTION_GET_CONTENT, that will tend to route
    directly to a system-supplied UI for obtaining content, bypassing any
    chooser, on Android 4.4+.
    https://stackoverflow.com/a/48045737
    * Therefore they not  « `always display the chooser` »
        contrary to they say at
        https://developer.android.com/training/sharing/send

#### Android Permissions

* First of all, read all my comments in
    ./app/src/main/java/fr/uga/julioju/taquingame/picture/PictureActivity.java
    It was an hard work !

* I've reported two issues on Google Issue tracker
    1. https://issuetracker.google.com/issues/114402174 named
        ` Some part of second example of Context.getExternalFilesDir are wrong`
    2. https://issuetracker.google.com/issues/114554343 relative to
        `Activity[Compat].shouldShowRequestPermissionRationale`
        * Response of the developer team:
            « *We intentionally do not want to explain in which cases this API
            returns true and false. This allows the API to implement more
            complex logic if needed.* »

* Interesting links:
    * https://github.com/googlesamples/android-RuntimePermissions/
    * https://developer.android.com/training/permissions/requesting#explain
        (very interesting)
    * https://github.com/googlesamples/android-RuntimePermissions/
        (official example)

* https://developer.android.com/guide/topics/media/camera#manifest
    «  If you are using the camera by invoking an existing camera app, your
    application does not need to request this permission. »

* Use the Gallery App not needs permission READ_EXTERNAL_STORAGE.
    Probably it's the same mechanism as when we use the Camera App,
    our App not needs to have camera permission
    https://developer.android.com/guide/topics/media/camera#manifest .
    * But, when the orientation of the Screen changes, and TaquinActivity
        is destroyed, then created again, the activity loose the read permission
        on the `android.net.Uri` (probably it looses the URI permission).
        * Maybe see
            https://developer.android.com/guide/topics/providers/content-provider-basics#Permissions
        * This problems doesn't occur for the `ContentProvider`,
            more precisely for the `DocumentProvider`, more precisely the app
            named "`Files`" version 8.0.0. It occurs with the `Gallery` version
            1.1.40030.

* As our app create a subfolder in
    `getExternalFilesDir(Environment.DIRECTORY_PICTURES)`,
    we need storage permission. Furthermore, the file is created by
    our app, not by the Camera app (I've tested, Camera app
    can't create a file when it's called from an other app).


#### Work with images and files
* To simply take a photo, read:
    * https://developer.android.com/training/camera/photobasics
    * https://developer.android.com/guide/components/intents-common#ImageCapture
* To pick a photo:
    https://developer.android.com/guide/topics/providers/document-provider
* To split images, read:
    http://www.chansek.com/splittingdividing-image-into-smaller/
* To Loading large image efficiently see:
    https://developer.android.com/topic/performance/graphics/load-bitmap
* **See very useful and complet examples at**
    https://developer.android.com/reference/android/content/Context#getExternalFilesDir(java.lang.String)
        * Maybe it's better to use `Context.getExternalFilesDir();` than
            `Context.getFilesDir()` because we could retrieve it
            thanks a `DocumentProvider` (but not by a gallery).
        * On emulator, the App seems not have simply read or write access to
            getFilesDir();
        * But be careful !
            « Shared storage may not always be available, since removable media
            can be ejected by the user. »
            * If I understand well if
                `Context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) ==
                    null`
                the external storage isn't mounted.
                TODO test in real device
            * Test write access thanks
                `Environment.getExternalStorageState(storageDir) ==
                    Environment.MEDIA_MOUNTED`.
            * In Genymotion, we can't start without `android_system_disk.vmdk`
                and the App is saved in this Virtual Disk.
            * In `Android Emulator AVD`, when we eject sd card, it works. In
                `Genymotion` we can't eject sd card.
            * TODO test it with a real Device.
* For dealing between private files of our app, and others app, we must use
    Uri with scheme `content://` and not Uri with scheme `file://`
    https://developer.android.com/guide/topics/providers/content-provider-basics#ContentURIs
    Otherwise we have:
    https://developer.android.com/reference/android/os/FileUriExposedException
    * We must first create an empty file, then send it
        to the camera App. If the Camera App doesn't take photo,
        this file stay empty.
    * « The ContentProvider basic protocol does not support browsing of
        directory structures this way »
        https://stackoverflow.com/questions/28009747/android-how-to-list-files-inside-a-folder-with-a-content-provider
        * Therefore following codes can't work:
            1.
                ```
                File storageDir = super.getExternalFilesDir(
                    Environment.DIRECTORY_PICTURES);
                intent.setDataAndType(storageDirUri, "image/*");
                ```
            2.
                ```
                    File storageDir = super.getExternalFilesDir(
                            Environment.DIRECTORY_PICTURES);
                    // https://developer.android.com/reference/android/provider/DocumentsContract#EXTRA_INITIAL_URI
                    // « The initial location is system specific if this extra is missing or
                    //     document navigator failed to locate the desired initial location.
                    // »
                    intent.putExtra(DocumentsContract.EXTRA_INITIAL_URI, storageDirUri);
                ```
            3. The following code can't work too even if the Uri has
                    scheme `content:///`
                ``
                    Intent mediaScanIntent = new Intent(Intent
                            .ACTION_MEDIA_SCANNER_SCAN_FILE);
                    Uri photoUriContentScheme = FileProvider.getUriForFile(this,
                            "fr.uga.julioju.taquingame.fileprovider",
                            photoFile);
                    mediaScanIntent.setData(photoUriContentScheme);
                    this.sendBroadcast(mediaScanIntent);
                ``
                See https://developer.android.com/guide/topics/providers/document-provider#open-client
        * ***Workaround for the demo***:
            As the `File` show the last folder used, before the demo:
                1. In the App that implements `DocumentsProvider`
                    in its settings click in `show internal storage`
                    Then on the new location that appears on the left:
                2. Navigate to `/Android/data/fr.uga.taquingame/files/Pictures`
* `DocumentsProvider` can't navigate from `/`, but only in `/storage`.
    `Amaze` is an App that doesn't implement a `DocumentsProvider`,
    but with it we could `root` folder.

* The file should be created before the camera app is launch, otherwise
    we have:
    ```
    09-08 13:11:40.644 1807-1807/com.android.camera2 E/CAM_StateSavePic: exception while saving result to URI: Optional.of(content://fr.uga.julioju.taquingame.fileprovider/images/taquinGame20180908_131135_/.jpg)
    java.io.FileNotFoundException: open failed: ENOENT (No such file or directory)
    ```

* Most File Managers don't support Open Folder Intent
    * Therefore the default Android File Manager doesn't interpret
        ```
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        Uri storageDirUri = Uri.parse(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES)
                .getPath());
        android.util.Log.d("uri Environment", storageDirUri.toString());
        intent.setDataAndType(storageDirUri, "image/*");
        ```
    * https://github.com/syncthing/syncthing-android/issues/838
    * https://stackoverflow.com/questions/48499726/open-specific-folder-in-file-manager-for-viewing
    * https://github.com/1hakr/AnExplorer/issues/96



### MediaScannerConnection / Intent.ACTION_MEDIA_SCANNER_SCAN_FILE
* ***See my issue at https://issuetracker.google.com/issues/114402174*** named
    « Some part of second example of Context.getExternalFilesDir are wrong. »
    ==> we can't use MediaScanner ! I've tested !

* Tested also with this function that could retrieve an `Uri` with scheme
    `content:///`. https://stackoverflow.com/a/14456406.
    But this function fail at line with `Cursor cursor` with error:
    ```
    java.lang.SecurityException: Permission Denial: reading com.android.providers.media.MediaProvider uri content://media/external/images/media from
    ```

* We can't use `Uri.fromFile(new File("/sdcard/cats.jpg"))` or
    `Uri.parse(new File("/sdcard/cats.jpg").toString()`
    because it can't work on Android API 26+.
    https://stackoverflow.com/questions/3004713/get-content-uri-from-file-path-in-android#comment88013079_3005936

### Class diagram
* https://en.wikipedia.org/wiki/Class_diagram
* https://www.cs.odu.edu/~zeil/cs330/s14/Public/classDiagrams/classDiagrams__html.html
* https://www.uml-diagrams.org/

### Notes misc
* « Although this is often more concise than a named class, for classes with
    only one method, even an anonymous class seems a bit excessive and
    cumbersome.  Lambda expressions let you express instances of single-method
    classes more compactly. »
    https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html

* OnClickListener
    Be careful, keep in mind that an instance of `OnClickListener` could be
    have a delay before to be garbaged.

* Android.util.Log.d
    * System.out.println could not be used. To se view in "Logcat",
        use Android.util.Log
    * Warning 1: Android.util.Log.d print nothing if second param is `null`.
    * Warning 2: Android.util.Log.d could mix two logs if they have the same
        first param : `e.g. Log.d("Title", "aa"); Log.d("Title", "bb");`

## My implementation
* See the comment of the class
    ./app/src/main/java/fr/uga/julioju/taquingame/taquin/TaquinActivity.java

### ./app/src/main/java/fr/uga/julioju/taquingame/taquin/TaquinActivity.java
* ***See Logcat*** to understand well how Barriers are build!
* See also comments in this file!
* ***See Logcat*** to obtain informations about a Square when we click on it.

### Square board prototype
* File that draw a square board in a console. For prototypage.
* Each square have a north, east, south, west square (or null).
* It's constructs with recursivity.
* Actually, square.north and square.west is always `null` (problematic).

## Tested
* Tested with Genymotion, with Android API 26. On a laptop with a full HD.
    1. « Custom Tablet - 8.0 - API 26 - 1536x2048 »
    2. « Custom Phone - 8.0 - API 26 - 768x1280 »
* Landscape mode is actually not optimised.
* It's sure, could not work with `API < 21` (Android 5.0)
* ~~Sometimes, when we try to load a picture, there is crashs.~~
    never experimented again.

* Sometimes we have the following error:
    ```
    09-17 14:12:22.029  1628  1628 E Decode image error: Error when try to retrieve selected picture. No picture is displayed in Squares.
    09-17 14:12:22.040  1628  1628 E Exception: java.io.IOException: Fail to decode bitmap from Urifile:///storage/emulated/0/Pictures/taquingame/taquinGame20180917_135820_6044549262338316943.jpg
    09-17 14:12:22.040  1628  1628 E Exception:     at fr.uga.julioju.taquingame.util.ImageUtil.decodeSampledBitmapFromStream(ImageUtil.java:200)
    09-17 14:12:22.040  1628  1628 E Exception:     at fr.uga.julioju.taquingame.util.ImageUtil.generateBitmapDrawableArray(ImageUtil.java:221)
    09-17 14:12:22.040  1628  1628 E Exception:     at fr.uga.julioju.taquingame.taquin.TaquinActivity.onCreate(TaquinActivity.java:355)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.app.Activity.performCreate(Activity.java:6975)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.app.Instrumentation.callActivityOnCreate(Instrumentation.java:1213)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.app.ActivityThread.performLaunchActivity(ActivityThread.java:2770)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.app.ActivityThread.handleLaunchActivity(ActivityThread.java:2892)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.app.ActivityThread.-wrap11(Unknown Source:0)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.app.ActivityThread$H.handleMessage(ActivityThread.java:1593)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.os.Handler.dispatchMessage(Handler.java:105)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.os.Looper.loop(Looper.java:164)
    09-17 14:12:22.040  1628  1628 E Exception:     at android.app.ActivityThread.main(ActivityThread.java:6541)
    09-17 14:12:22.040  1628  1628 E Exception:     at java.lang.reflect.Method.invoke(Native Method)
    09-17 14:12:22.040  1628  1628 E Exception:     at com.android.internal.os.Zygote$MethodAndArgsCaller.run(Zygote.java:240)
    09-17 14:12:22.040  1628  1628 E Exception:     at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:767)
    ```
    * note : now line 202 is 208 with:
        ```
        if (image == null) {
            throw new IOException(messageError);
        }
        ```
    * To resolve this, I've added a loop that retry this five times with
        delays.

## TODO
* Make an abstract class ActiviyException and replace PictureActivity by
    ActiviyException in ImageUtil + see TODO in TaquinActivity.  It's not a very
    important todo, because generally there isn't error when a file is decoded
    from URI. If there is an error, it's could be a bug  by Android !  Or maybe
    the more serious error it's if a SecurityException (URI permission lost) is
    raised or if the photo is deleted.
    It's an very easy TODO.
    * To avoid this problem, maybe photo could be temporally copied in
        `Context.getFilesDir()`. Then the commit of september, 25 could be
        reverted and the code simplified.. `Context.getFilesDir()` is only
        accessible with root access by a File explorer. As by default no File
        explorer are installed that allow root access, standard user can't
        delete it.
