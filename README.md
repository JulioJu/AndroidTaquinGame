# TP Développement Web Mobile : Création d'un jeu de taquin

## Teacher's instruction
Projet – Application Android

### But du TP :
--> Manipuler les widgets graphiques et la gestion d'événement sur android.

### Le Principe de TP :
1. ~~Vous devez réaliser dans ce TP une prémisse de jeu de taquin. Pour cela, il vous faut comprendre le tutoriel sur le gestionnaire de placement `GridView` d'Android :
http://developer.android.com/guide/topics/ui/layout/gridview.html~~. Not done like it

2. Contrairement au tutoriel précédent, vous avez une seule image à la place d'un ensemble de fichiers images dans `res/drawable`. Il va donc falloir découper cette grande image en une grille 3x3 petites images.
Pour faire cela, vous avez à votre disposition les méthodes suivantes :
    * Permet de charger l'image indexée par `R.drawable.android` dans un objet de type Bitmap. C'est un objet de type `Context`
        `Bitmap img = BitmapFactory.decodeResource(c.getResources(), R.drawable.android);`
    * Permet de découper un bout d'image de taille height x width à partir du point `(x,y)`
        `Bitmap unBout = Bitmap.createBitmap(img, x, y, width,height);`
    * Permet de créer une image noir de taille  height x width `Bitmap unAutreBout = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);`

Vous placerez chaque bout d'image dans un tableau de Bitmap (`Bitmap[]`) contrairement au tutoriel qui utilise un tableau de `int` contenant les références (indexées) vers les images. Ainsi, il faudra utiliser la méthode `imageView.setImageBitmap(...)` à la place de la méthode  `imageView.setImageResource(...)` dans `getView`.

Testez votre application. Vérifiez que le découpage se déroule comme prévu. Modifiez les paramètre du `GridView` dans les fichiers `main.xml` et dans l'activité afin de réduire au maximum les espaces entre chaque image.

3. Une fois que vous avez réussi à découper une image et à afficher tous les petits bouts dans l'ordre, vous allez créez l'écouteur sur les événement clavier. Pour cela, je vous conseille de faire en sorte que votre activité implémente l'interface `OnKeyListener`. Ajoutez le nécessaire dans l'activité pour enregistrer votre écouteur auprès du `GridView`.

* Dans la méthode appelée lors d'une saisie clavier, vous aurez entre autre à votre disposition les variables : `int keyCode` et `KeyEvent event`
    * La première représente le code de la touche que l'on a tapé.
    * L'objet de type `KeyEvent` permet de savoir quel type d'événement clavier est arrivé :
        * touche enfoncée (`event.getAction()==KeyEvent.ACTION_DOWN`),
        * touche relâchée (`event.getAction()==KeyEvent.ACTION_UP`), etc.

* Pour savoir si la touche « flèche vers le haut » a été saisie, vous avez à réaliser le test suivant :
`keycode==KeyEvent.KEYCODE_DPAD_DOWN`.

* A la fin des modifications, il faut appeler la méthode `this.notifyDataSetChanged();` pour que la vue soit mise à jour.

4. A partir de là, je vous laisse réaliser le code nécessaire pour que votre jeu de taquin soit fonctionnel.


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
* **./app/src/main/res/layout/activity_main.xml** could be understood thanks this
comments

* How I don't use `Grid Layout` as asked by the teacher, because:
    « *Note: For better performance and tooling support, you should instead build
    your layout with ConstraintLayout.* » (read
    https://developer.android.com/guide/topics/ui/layout/gridview)

* Thanks `android.support.constraint.Barrier` appeared since
        `Constraint Layout 1.1.0 beta 1` (see [this release note](https://androidstudio.googleblog.com/2017/05/constraintlayout-110-beta-1-release.html)), we could easy draw a Grid.
        `Constraint Layout 1.1.0` is available since Thursday, April 12, 2018 (see
        [this release not](https://androidstudio.googleblog.com/2018/04/constraintlayout-110.html))
    **Ref:**  https://stackoverflow.com/questions/42846261/trying&#45;to&#45;replicate&#45;gridlayout&#45;column&#45;alignment&#45;with&#45;constraintlayout


* Official guide than explain what is a Layout (very interesting)
    https://developer.android.com/guide/topics/ui/declaring-layout

* ConstraintLayout vs RelativeLayout
    https://stackoverflow.com/a/46330178

* Constraint Layout API Reference
    https://developer.android.com/reference/android/support/constraint/ConstraintLayout

* To understand attribute android:orientation
    https://developer.android.com/reference/android/widget/LinearLayout

* To understand android.support.constraint.ConstraintLayout
    https://developer.android.com/reference/android/support/constraint/Barrier

* In Constraint Layout: Barrier vs Guideline:
    https://stackoverflow.com/questions/47114672/what&#45;is&#45;difference&#45;between&#45;barrier&#45;and&#45;guideline&#45;in&#45;constraint&#45;layout
    « *The only difference between Barrier and Guideline is that Barrier's
    position is flexible and always based on the size of multiple UI elements
    contained within it and Guideline's position is always fixed.* »

* **Source Code**:
    https://android.googlesource.com/platform/frameworks/opt/sherpa/+/studio-3.1.2

* To understand START, END, BOTTOM, LEFT, RIGHT, START constants, read
    https://developer.android.com/reference/android/support/constraint/ConstraintLayout.LayoutParams

* To understand WRAP_CONTENT and MATCH_PARENT constants, read
    https://developer.android.com/reference/android/support/constraint/ConstraintLayout.LayoutParams

* To understand constructor ConstraintLayout.LayoutParams(int, int), read
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
    (Founded at https://stackoverflow.com/a/13930148).
    * This resource is also interesting
        https://stackoverflow.com/questions/3496269/how-do-i-put-a-border-around-an-android-textview

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

## TODO
* Keep `Square[][] grid` when the Activity is stopped (but resize Squares
    when it's simply the Orientation (vertical / horizontal) that changes)
* Add levels. Level beginner, intermediate, master. Or let the gamer
    choose the length of the grid.
* Add winner message.
