# TP Développement Web Mobile : Création d'un jeu de taquin

## Teacher's instruction
Développement Web mobile
2017-2018
Android – Projet Taquin

Sources:
http://imss-www.upmf-grenoble.fr/~davidjer/dwm/Projet-Taquin2017.pdf
### Consignes
## Fonctionnalités
* On peut choisir soit une image de la galerie du téléphone, soit prendre
    une photo.
* On peut configurer la taille de la grille (2x2, 3x3, 4x4).
* L'image doit être bien orientée (mode paysage ou portrait,
    d'après infos EXIF).
* L'image doit être adaptée à la taille et résolution de l'écran (pas d'image
    trop pixelisée ou trop grande)
* Lorsque le jeux est gagné, un message doit être affiché puis une option pour
recommencer ou quitter l'appli doit être proposée.

### Les plus :
* une animation lors du déplacement d'un bout d'image
    http://developer.android.com/reference/android/view/animation/TranslateAnimation.html
* faire tourner l'application quand on tourne le téléphone.

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

### Genymotion and IntelliJ
* On my computer, Genymotion is very faster and lighter (for the CPU) than
    the Android Emulator. I've also heard than it's very more reliable.
* It's free. And with a registration, you could have lot of different
    Virtual Images.
* **For Intellij Ultimate Edition, you must configure the debugger. See:
    https://stackoverflow.com/a/51476111**
    Read also the comments of this comment !
* When import, we must build to see some errors disappear.
* I've experienced errors with `Google AVD` (Android Emulator)
    https://stackoverflow.com/questions/38450717/session-app-error-while-installing-apk
    Not read all the comments. I've only experienced to disable `Instant run`
    (no try others complex solutions).
    But Instant Run (`Tools --> Apply changes`) is very useful ! Therefore
    kicked off `Google Emulator`.

### Examples downloaded at Google Samples
* An easy solution to test examples at https://github.com/googlesamples/
    without compatibilities errors (build-tools, platforms, gradle, etc.)
    is simply :
    1. create an empty project in the IDE.
    2. `rm -R project_name/app/src/main/*`
    3. `cp -R google_sample/Application/src/main/* projectname/app/src/main`

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

## Notes:
* For Square.java:
    * « match_parent is not supported. With 0dp, you can think of your
        constraints as 'scalable' rather than 'filling whats left'. »
        https://stackoverflow.com/questions/37603751/set-width-to-match-constraints-in-constraintlayout
    * As squareWidth is the result of an Integer division, it could be have
        space at the right of the screen and at the bottom of the screen.
        To correct this, I've added a `margin_left` to Square on the
        first column, and a `margin_top` to Square on the first row. I've
        also added a background for the activity, to not have white background.

* OnClickListener
    Be careful, keep in mind that an instance of `OnClickListener` could be
    have a delay before to be garbaged.

* Android.util.Log.d
    * System.out.println could not be used. To se view in "Logcat",
        use Android.util.Log
    * Warning 1: Android.util.Log.d print nothing if second param is `null`.
    * Warning 2: Android.util.Log.d could mix two logs if they have the same
        first param : `e.g. Log.d("Title", "aa"); Log.d("Title", "bb");`

* ConstraintSet should be set after layout.addView(view);

* When we display size resolution in a Toast at the launch of
    TaquinActivity, the `hight` is lower than the real height.
    It's because:
    « The returned size may be adjusted to exclude certain system decor
    elements that are always visible ». For example, Navigation bar !
    https://developer.android.com/reference/android/view/Display

## My implementation
* See the comment of the class
    ./app/src/main/java/fr/uga/julioju/taquingame/MainActivity.java

#### ./app/src/main/java/fr/uga/julioju/taquingame/MainActivity.java
* ***See Logcat*** to understand well how Barriers are build!
* See also comments in this file!
* ***See Logcat*** to obtain informations about a Square when we click on it.

## Square board prototype
* File that draw a square board in a console. For prototypage.
* Each square have a north, east, south, west square (or null).
* It's constructs with recursivity.
* Actually, square.north and square.west is always `null` (problematic).
