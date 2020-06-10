package nl.vu.cs.s2group.nappa.plugin.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.PsiStatement;
import org.jetbrains.annotations.NotNull;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Provides a self-contained {@link StringBuilder} to construct the result message for when finishing instrumenting.
 * Provides abstraction to present a result dialog
 */
public class InstrumentResultMessage {
    private final StringBuilder builder;

    /**
     * Count of statements instrumented in this run
     */
    private int instrumentationCount;

    /**
     * Count of statements that can be instrumented in this run
     */
    private int possibleInstrumentationCount;

    /**
     * Count of statements that were instrumented in previous runs
     */
    private int alreadyInstrumentedCount;

    /**
     * Count of statements that do not need to be instrumented.
     */
    private int unneededInstrumentationCount;

    public InstrumentResultMessage() {
        builder = new StringBuilder();
        instrumentationCount = 0;
        possibleInstrumentationCount = 0;
        alreadyInstrumentedCount = 0;
        unneededInstrumentationCount = 0;
    }

    /**
     * Returns the text of the result message.
     *
     * @return the result message
     */
    public String getMessage() {
        addInstrumentationOverview();
        return builder.toString();
    }

    /**
     * Increment by 1 the count of statement instrumented in this run
     *
     * @return A instance of this object
     */
    public InstrumentResultMessage incrementInstrumentationCount() {
        instrumentationCount++;
        return this;
    }

    /**
     * Increment by 1 the count of statement that can be instrumented
     *
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage incrementPossibleInstrumentationCount() {
        possibleInstrumentationCount++;
        return this;
    }

    /**
     * Increment by 1 the count of statement that do not need to be instrumented
     *
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage incrementUnneededInstrumentationCount() {
        unneededInstrumentationCount++;
        return this;
    }

    /**
     * Increment by 1 the count of statement that were already instrumented
     *
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage incrementAlreadyInstrumentedCount() {
        alreadyInstrumentedCount++;
        return this;
    }

    /**
     * Insert a overview of the instrumentation in the begging of the result message
     */
    private void addInstrumentationOverview() {
        StringBuilder message = new StringBuilder();

        message.append(possibleInstrumentationCount)
                .append(" statements can be instrumented.")
                .append("\n")
                .append(instrumentationCount)
                .append(" statements were instrumented in this run.")
                .append("\n")
                .append(alreadyInstrumentedCount)
                .append(" statements were already instrumented.")
                .append("\n")
                .append(unneededInstrumentationCount)
                .append(" statements do not need to be instrumented.")
                .append("\n\n");

        builder.insert(0, message);
    }

    /**
     * Append the class or interface qualified name to the result message
     *
     * @param psiClass A Java class or interface.
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage appendPsiClass(@NotNull PsiClass psiClass) {
        builder.append("Class: ").append(psiClass.getQualifiedName()).append("\n");
        return this;
    }

    /**
     * Append the method or constructor qualified name to the result message
     *
     * @param psiMethod A Java method or constructor.
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage appendPsiMethod(@NotNull PsiMethod psiMethod) {
        builder.append("Method: ").append(psiMethod.getName()).append("\n");
        return this;
    }

    /**
     * Append a free text to the result message
     *
     * @param text The text to append.
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage appendText(String text) {
        builder.append(text).append("\n");
        return this;
    }

    /**
     * Append a string to inform the class initializer was instrumented to the result message
     *
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage appendPsiClassInitializer() {
        builder.append("Initializer block: ").append("\n");
        return this;
    }

    /**
     * Append the field name to the result message
     *
     * @param psiField A Java field or enum constant.
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage appendPsiField(@NotNull PsiField psiField) {
        builder.append("Field: ").append(psiField.getName()).append("\n");
        return this;
    }

    /**
     * Append the statement text to the result message
     *
     * @param psiStatement A Java statement.
     * @return A instance of this object
     */
    public InstrumentResultMessage appendPsiStatement(@NotNull PsiStatement psiStatement) {
        builder.append("Statement: ").append(psiStatement.getText()).append("\n");
        return this;
    }

    /**
     * Append a block separator to the result message
     *
     * @return A instance of this object
     */
    @SuppressWarnings("UnusedReturnValue")
    public InstrumentResultMessage appendNewBlock() {
        builder.append("\n");
        return this;
    }

    /**
     * Takes the constructed message in {@code builder} and make a dialog with the result
     *
     * @param project An object representing an IntelliJ project.
     * @param title   The title of the dialog
     */
    public void showResultDialog(Project project, String title) {
        Messages.showMessageDialog(project, getMessage(), title, Messages.getInformationIcon());
    }

    /**
     * Make a error dialog with the {@code exception} content
     *
     * @param project   An object representing an IntelliJ project.
     * @param exception The caught exception
     * @param title     The title of the dialog
     */
    public void showErrorDialog(Project project, @NotNull Exception exception, String title) {
        StringWriter errors = new StringWriter();
        exception.printStackTrace(new PrintWriter(errors));
        Messages.showErrorDialog(project, errors.toString(), title);
    }
}
