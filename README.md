# TP Développement Web Mobile : Création d'un jeu de taquin

## Teacher's instruction

Projet – Application Android

### But du TP :

--> Manipuler les widgets graphiques et la gestion d'événement sur android.

### Le Principe de TP :

1. Vous devez réaliser dans ce TP une prémisse de jeu de taquin. Pour cela, il vous faut comprendre le tutoriel sur le gestionnaire de placement `GridView` d'Android :
http://developer.android.com/guide/topics/ui/layout/gridview.html

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
* My comments in ./app/src/main/AndroidManifest.xml could be interesting.




