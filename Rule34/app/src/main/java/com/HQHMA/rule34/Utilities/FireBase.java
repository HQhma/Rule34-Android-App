package com.HQHMA.rule34.Utilities;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class FireBase {

    public static DocumentReference getAppData(){
        return FirebaseFirestore.getInstance().collection("app").document("version");
    }

    public static CollectionReference allUserCollectionReference(){
        return FirebaseFirestore.getInstance().collection("users");
    }

}
