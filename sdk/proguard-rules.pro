# Specify the input jars, output jars, and library jars.
# In this case, the input jar is the program library that we want to process.

# Save the obfuscation mapping to a file, so we can de-obfuscate any stack
# traces later on. Keep a fixed source file attribute and all line number
# tables to get line numbers in the stack traces.
# You can comment this out if you're not interested in stack traces.

-printmapping out.map
-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,SourceFile,LineNumberTable,EnclosingMethod

# Preserve all annotations.

-keepattributes *Annotation*

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class * {
    public protected *;
}

# Preserve all .class method names.

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Preserve the special static methods that are required in all enumeration
# classes.

-keepclassmembers class * extends java.lang.Enum {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Your library may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

-keep public class co.omise.android.** { *; }
-keep public interface co.omise.android.** { *; }
-keep public class * implements co.omise.android.** { *; }

# Keep everyting in the Netcetera Android 3DS SDK package
-keep public class com.netcetera.threeds.sdk.** {
  public protected *;
}
-keepnames class com.netcetera.threeds.sdk.** { *; }

# Don't warn about any unused code from the Netcetera Android 3DS SDK package
-dontwarn com.netcetera.threeds.sdk.**

# Keep everyting in Guardsquare Dexguard
-keep public class com.guardsquare.dexguard.** {
  public protected *;
}

# Keep everything from bouncycastle
-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

# Keep the classes from slf4j
-keep class org.slf4j.** { *; }

# Keep kotlin.KotlinVersion if present
-keep class kotlin.KotlinVersion { *; }
-dontwarn kotlin.KotlinVersion

