package com.translator.service.codactor.editor.psi;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.search.searches.OverridingMethodsSearch;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.Query;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public class PsiElementCollectorServiceImpl implements PsiElementCollectorService {
    private final PsiFileService psiFileService;

    @Inject
    public PsiElementCollectorServiceImpl(PsiFileService psiFileService) {
        this.psiFileService = psiFileService;
    }

    /*public List<PsiElement> getAllPsiElementsWithinRange(String filePath, int startOffset, int endOffset) {
        PsiFile psiFile = psiFileService.getPsiFileFromPath(filePath);
        List<PsiElement> elements = new ArrayList<>();

        PsiElement startElement = psiFile.findElementAt(startOffset);
        PsiElement endElement = psiFile.findElementAt(endOffset);

        if (startElement != null && endElement != null) {
            PsiElement commonParent = PsiTreeUtil.findCommonParent(startElement, endElement);

            if (commonParent != null) {
                collectElementsWithinRange(commonParent, startOffset, endOffset, elements);
            }
        }

        return elements;
    }

    private void collectElementsWithinRange(PsiElement element, int startOffset, int endOffset, List<PsiElement> elements) {
        if (element.getTextRange().getStartOffset() >= startOffset && element.getTextRange().getEndOffset() <= endOffset) {
            System.out.println("Element: " + element.getText());
            System.out.println("Element type: " + element.getClass());
            elements.add(element);
        }

        for (PsiElement child = element.getFirstChild(); child != null; child = child.getNextSibling()) {
            collectElementsWithinRange(child, startOffset, endOffset, elements);
        }
    }*/
    public List<PsiElement> collectMethodsAndVariables(String filePath, int startIndex, int endIndex) {
        PsiFile psiFile = psiFileService.getPsiFileFromPath(filePath);
        List<PsiElement> collectedElements = new ArrayList<>();

        PsiElement startElement = psiFile.findElementAt(startIndex);
        PsiElement endElement = psiFile.findElementAt(endIndex);

        if (startElement != null && endElement != null) {
            PsiElement commonParent = PsiTreeUtil.findCommonParent(startElement, endElement);

            if (commonParent != null) {
                PsiTreeUtil.processElements(commonParent, element -> {
                    if (element instanceof PsiField) {
                        int elementStartOffset = element.getTextRange().getStartOffset();
                        int elementEndOffset = element.getTextRange().getEndOffset();

                        if (!(elementEndOffset < startIndex || elementStartOffset > endIndex)) {
                            collectedElements.add(element);
                        }
                    }
                    return true;
                });
            }
        }

        List<PsiMethod> intersectingMethods = getIntersectingMethods(filePath, startIndex, endIndex);
        for (PsiMethod intersectingMethod : intersectingMethods) {
            PsiMethod interfaceMethod = findInterfaceMethod(intersectingMethod);
            collectedElements.add(Objects.requireNonNullElse(interfaceMethod, intersectingMethod));
        }

         return collectedElements;
     }

    /*public List<PsiElement> collectGlobalReferencesToClass(PsiClass targetClass) {
        List<PsiElement> globalReferences = new ArrayList<>();
        Project project = targetClass.getProject();

        PsiShortNamesCache.getInstance(project).processElementsWithName(
                targetClass.getName(),
                (PsiElement psiElement) -> {
                    if (psiElement instanceof PsiReference) {
                        PsiReference reference = (PsiReference) psiElement;
                        if (reference.resolve() == targetClass) {
                            globalReferences.add(psiElement);
                        }
                    }
                    return true;
                },
                GlobalSearchScope.projectScope(project),
                null
        );

        return globalReferences;
    }*/

    public List<PsiReference> collectGlobalReferencesToClass(PsiClass targetClass) {
        List<PsiReference> globalReferences = new ArrayList<>();

        // Search for references to the target class across the whole project
        Query<PsiReference> search = ReferencesSearch.search(targetClass);

        // Collect the found references
        Collection<PsiReference> references = search.findAll();
        globalReferences.addAll(references);

        return globalReferences;
    }


    public List<PsiMethod> getIntersectingMethods(String filePath, int startIndex, int endIndex) {
        List<PsiMethod> intersectingMethods = new ArrayList<>();

        PsiFile psiFile = psiFileService.getPsiFileFromPath(filePath);

        Collection<PsiMethod> allMethods = PsiTreeUtil.findChildrenOfType(psiFile, PsiMethod.class);
        for (PsiMethod method : allMethods) {
            if (isMethodInGivenRange(method, startIndex, endIndex)) {
                intersectingMethods.add(method);
            }
        }

        return intersectingMethods.isEmpty() ? new ArrayList<>() : intersectingMethods;
    }


    private boolean isMethodInGivenRange(PsiMethod methodToCheck, int startIndex, int endIndex) {
        TextRange declarationRange;
        if (methodToCheck.getBody() != null) {
            declarationRange = new TextRange(
                    methodToCheck.getTextRange().getStartOffset(),
                    methodToCheck.getTextRange().getEndOffset()
            );
        } else {
            // Handling abstract methods or interface methods
            declarationRange = methodToCheck.getTextRange();
        }

        // Check if the specified range intersects with the method declaration range
        return declarationRange.intersects(new TextRange(startIndex, endIndex));
     }


    public PsiMethod findInterfaceMethod(PsiMethod implementationMethod) {
        PsiMethod[] superMethods = implementationMethod.findSuperMethods();

        // Checking if there are any super methods (interface methods in this case)
        if (superMethods.length > 0) {
            // Returning the first super method found, you may want to handle multiple super methods differently
            return superMethods[0];
        }

        // Returning null if there are no super methods
        return null;
    }

    public PsiMethod findMethodDeclaration(PsiMethodCallExpression methodCallExpression) {
        return methodCallExpression.resolveMethod();
    }

    public Collection<PsiMethod> findImplementations(PsiMethod method) {
        // Search for overriding methods
        Query<PsiMethod> search = OverridingMethodsSearch.search(method);

        for (PsiMethod overridingMethod : search) {
            PsiClass containingClass = overridingMethod.getContainingClass();
            if (containingClass != null) {
                System.out.println("Implementation found in class: " +
                        containingClass.getQualifiedName() +
                        ", method: " + overridingMethod.getName() + " : " + overridingMethod.getText());
            }
        }
        return search.findAll();
    }

    public Collection<PsiReference> findMethodReferences(PsiMethod psiMethod) {
        Query<PsiReference> search = ReferencesSearch.search(psiMethod);
        System.out.println("Search: " + search.findAll().size());
        for (PsiReference reference : search) {
            PsiElement element = reference.getElement();

            System.out.println("Reference found in file: " +
                    element.getContainingFile().getName() +
                    ", at offset: " + element.getTextOffset());
        }
        return search.findAll();
    }

    public Collection<PsiReference> findClassReferences(PsiClass psiClass) {
        Query<PsiReference> search = ReferencesSearch.search(psiClass);

        for (PsiReference reference : search) {
            PsiElement element = reference.getElement();

            // Check if the reference is in the specified file
            System.out.println("Reference found in file: " +
                    element.getContainingFile().getName() +
                    ", at offset: " + element.getTextOffset());
        }
        Collection<PsiReference> references = search.findAll();

        PsiClass superClass = psiClass.getSuperClass();
        if (superClass != null) {
            Query<PsiReference> superClassSearch = ReferencesSearch.search(superClass);
            references.addAll(superClassSearch.findAll());
        }
        return references;
    }

    public PsiClass findReferencedClassOfField(PsiField psiField) {
        PsiType fieldType = psiField.getType();

        if (fieldType instanceof PsiClassType) {
            PsiClassType classType = (PsiClassType) fieldType;
            return classType.resolve();
        }

        return null; // Return null if the type of the field is not a class type
    }

    public PsiClass findClassOfField(PsiField psiField) {
        return psiField.getContainingClass();
    }

    public Collection<PsiReference> findFieldReferences(PsiField psiField) {  // Added method to find field references
        Query<PsiReference> search = ReferencesSearch.search(psiField);
        System.out.println("Search: " + search.findAll().size());
        for (PsiReference reference : search) {
            PsiElement element = reference.getElement();

            System.out.println("Reference found in file: " +
                    element.getContainingFile().getName() +
                    ", at offset: " + element.getTextOffset());

        }

        return search.findAll();
    }
}
