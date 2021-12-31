package org.springframework.core;

public abstract class JdkVersion
{
    public static final int JAVA_13 = 0;
    public static final int JAVA_14 = 1;
    public static final int JAVA_15 = 2;
    public static final int JAVA_16 = 3;
    public static final int JAVA_17 = 4;

    private static final String javaVersion;
    private static final int majorJavaVersion;

    static
    {
        javaVersion = System.getProperty("java.version");

        if (javaVersion.indexOf("1.7.") != -1)
        {
            majorJavaVersion = JAVA_17;
        } else if (javaVersion.indexOf("1.6.") != -1) {
            majorJavaVersion = JAVA_16;
        } else if (javaVersion.indexOf("1.5.") != -1) {
            majorJavaVersion = JAVA_15;
        } else if (javaVersion.indexOf("1.4.") != -1) { // new
            majorJavaVersion = JAVA_14;                 // new
        } else {
            majorJavaVersion = JAVA_17;                 // changed from JAVA_14
        }
    }

    public static String getJavaVersion()
    {
        return javaVersion;
    }

    public static int getMajorJavaVersion()
    {
        return majorJavaVersion;
    }

    public static boolean isAtLeastJava14()
    {
        return true;
    }

    public static boolean isAtLeastJava15()
    {
        return getMajorJavaVersion() >= JAVA_15;
    }

    public static boolean isAtLeastJava16()
    {
        return getMajorJavaVersion() >= JAVA_16;
    }
}