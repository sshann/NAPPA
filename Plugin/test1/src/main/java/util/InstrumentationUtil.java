package util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * An class containing common utility methods to simplify the instrumentation actions
 */
public final class InstrumentationUtil {

    /**
     * Scan the project directory for all source files to search for all Java source files
     *
     * @param project An object representing an IntelliJ project.
     * @return A list of all Java source files in the project
     */
    public static @NotNull List<PsiFile> getAllJavaFilesInProjectAsPsi(Project project) {
        List<PsiFile> psiFiles = new LinkedList<>();
        String[] fileNames = FilenameIndex.getAllFilenames(project);

        fileNames = Arrays.stream(fileNames)
                .filter(fileName -> fileName.contains(".java"))
                .toArray(String[]::new);

        for (String fileName : fileNames) {
            PsiFile[] psiJavaFiles = FilenameIndex.getFilesByName(project, fileName, GlobalSearchScope.projectScope(project));
            // Remove the files from the NAPPA library from the list to process
            psiJavaFiles = Arrays.stream(psiJavaFiles)
                    .filter(psiJavaFile -> !((PsiJavaFile) psiJavaFile).getPackageName().contains("nl.vu.cs.s2group"))
                    .toArray(PsiFile[]::new);

            psiFiles.addAll(Arrays.asList(psiJavaFiles));
        }

        return psiFiles;
    }

    /**
     * Iterate the Java files structure until reaching the statement level (e.g. {@code String a = "text"}).
     * Upon reaching a statement, invokes the {@code callback} function passing the statement as parameter
     *
     * @param psiFiles    A list of all Java files within a project
     * @param fileFilter  Skip all files that does not contain any of the strings in the provided array
     * @param classFilter Skip all classes that does not contain any of the strings in the provided array
     * @param callback    A callback function invoked for each statement found in all files
     */
    public static void scanPsiFileStatement(@NotNull List<PsiFile> psiFiles, String[] fileFilter, String[] classFilter, Consumer<PsiStatement> callback) {
        for (PsiFile psiFile : psiFiles) {
            if (Arrays.stream(fileFilter).noneMatch(psiFile.getText()::contains)) continue;
            PsiClass[] psiClasses = ((PsiJavaFile) psiFile).getClasses();
            for (PsiClass psiClass : psiClasses) {
                scanPsiClass(psiClass, classFilter, callback);
            }
        }
    }

    /**
     * Auxiliary method for {@link InstrumentationUtil#scanPsiFileStatement} to be able to scan inner classes
     *
     * @param psiClass    A Java class
     * @param classFilter Skip all classes that does not contain any of the strings in the provided array
     * @param callback    A callback function invoked for each statement found in all files
     */
    private static void scanPsiClass(PsiClass psiClass, String[] classFilter, Consumer<PsiStatement> callback) {
        if (Arrays.stream(classFilter).noneMatch(psiClass.getText()::contains)) return;

        PsiMethod[] psiMethods = psiClass.getMethods();
        for (PsiMethod psiMethod : psiMethods) {
            if (psiMethod.getBody() == null) continue;
            PsiStatement[] psiStatements = psiMethod.getBody().getStatements();
            for (PsiStatement statement : psiStatements) {
                callback.accept(statement);
            }
        }

        PsiClass[] psiClasses = psiClass.getInnerClasses();
        for (PsiClass innerPsiClass : psiClasses) {
            scanPsiClass(innerPsiClass, classFilter, callback);
        }
    }
}
