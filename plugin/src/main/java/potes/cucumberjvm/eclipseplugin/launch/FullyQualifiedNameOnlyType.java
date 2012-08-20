/*
   Copyright 2012 James Phillpotts

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/


package potes.cucumberjvm.eclipseplugin.launch;

import java.io.InputStream;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.jdt.core.CompletionRequestor;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.ICompletionRequestor;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IInitializer;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IOpenable;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.ITypeParameter;
import org.eclipse.jdt.core.ITypeRoot;
import org.eclipse.jdt.core.IWorkingCopy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.WorkingCopyOwner;

class FullyQualifiedNameOnlyType implements IType {
	
	private String fqn;

	public FullyQualifiedNameOnlyType(String fqn) {
		this.fqn = fqn;
	}
	
	public String getFullyQualifiedName() {
		return fqn;
	}

	public boolean exists() {
		throw new UnsupportedOperationException();
	}

	public IJavaElement getAncestor(int arg0) {
		throw new UnsupportedOperationException();
	}

	public String getAttachedJavadoc(IProgressMonitor arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IResource getCorrespondingResource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String getElementName() {
		throw new UnsupportedOperationException();
	}

	public int getElementType() {
		throw new UnsupportedOperationException();
	}

	public String getHandleIdentifier() {
		throw new UnsupportedOperationException();
	}

	public IJavaModel getJavaModel() {
		throw new UnsupportedOperationException();
	}

	public IJavaProject getJavaProject() {
		throw new UnsupportedOperationException();
	}

	public IOpenable getOpenable() {
		throw new UnsupportedOperationException();
	}

	public IJavaElement getParent() {
		throw new UnsupportedOperationException();
	}

	public IPath getPath() {
		throw new UnsupportedOperationException();
	}

	public IJavaElement getPrimaryElement() {
		throw new UnsupportedOperationException();
	}

	public IResource getResource() {
		throw new UnsupportedOperationException();
	}

	public ISchedulingRule getSchedulingRule() {
		throw new UnsupportedOperationException();
	}

	public IResource getUnderlyingResource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}

	public boolean isStructureKnown() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public Object getAdapter(Class arg0) {
		throw new UnsupportedOperationException();
	}

	public ISourceRange getNameRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String getSource() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ISourceRange getSourceRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void copy(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void delete(boolean arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void move(IJavaElement arg0, IJavaElement arg1, String arg2, boolean arg3, IProgressMonitor arg4) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void rename(String arg0, boolean arg1, IProgressMonitor arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IJavaElement[] getChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean hasChildren() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String[] getCategories() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IClassFile getClassFile() {
		throw new UnsupportedOperationException();
	}

	public ICompilationUnit getCompilationUnit() {
		throw new UnsupportedOperationException();
	}

	public IType getDeclaringType() {
		throw new UnsupportedOperationException();
	}

	public int getFlags() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ISourceRange getJavadocRange() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public int getOccurrenceCount() {
		throw new UnsupportedOperationException();
	}

	public IType getType(String arg0, int arg1) {
		throw new UnsupportedOperationException();
	}

	public ITypeRoot getTypeRoot() {
		throw new UnsupportedOperationException();
	}

	public boolean isBinary() {
		throw new UnsupportedOperationException();
	}

	public IAnnotation getAnnotation(String arg0) {
		throw new UnsupportedOperationException();
	}

	public IAnnotation[] getAnnotations() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5, boolean arg6, ICompletionRequestor arg7)
			throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5, boolean arg6, CompletionRequestor arg7)
			throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5, boolean arg6,
			ICompletionRequestor arg7, WorkingCopyOwner arg8) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5, boolean arg6, CompletionRequestor arg7,
			IProgressMonitor arg8) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5, boolean arg6, CompletionRequestor arg7,
			WorkingCopyOwner arg8) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public void codeComplete(char[] arg0, int arg1, int arg2, char[][] arg3, char[][] arg4, int[] arg5, boolean arg6, CompletionRequestor arg7,
			WorkingCopyOwner arg8, IProgressMonitor arg9) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IField createField(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IInitializer createInitializer(String arg0, IJavaElement arg1, IProgressMonitor arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IMethod createMethod(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IType createType(String arg0, IJavaElement arg1, boolean arg2, IProgressMonitor arg3) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IMethod[] findMethods(IMethod arg0) {
		throw new UnsupportedOperationException();
	}

	public IJavaElement[] getChildrenForCategory(String arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IField getField(String arg0) {
		throw new UnsupportedOperationException();
	}

	public IField[] getFields() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String getFullyQualifiedName(char arg0) {
		throw new UnsupportedOperationException();
	}

	public String getFullyQualifiedParameterizedName() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IInitializer getInitializer(int arg0) {
		throw new UnsupportedOperationException();
	}

	public IInitializer[] getInitializers() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String getKey() {
		throw new UnsupportedOperationException();
	}

	public IMethod getMethod(String arg0, String[] arg1) {
		throw new UnsupportedOperationException();
	}

	public IMethod[] getMethods() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IPackageFragment getPackageFragment() {
		throw new UnsupportedOperationException();
	}

	public String[] getSuperInterfaceNames() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String[] getSuperInterfaceTypeSignatures() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String getSuperclassName() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String getSuperclassTypeSignature() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public IType getType(String arg0) {
		throw new UnsupportedOperationException();
	}

	public ITypeParameter getTypeParameter(String arg0) {
		throw new UnsupportedOperationException();
	}

	public String[] getTypeParameterSignatures() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeParameter[] getTypeParameters() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String getTypeQualifiedName() {
		throw new UnsupportedOperationException();
	}

	public String getTypeQualifiedName(char arg0) {
		throw new UnsupportedOperationException();
	}

	public IType[] getTypes() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isAnnotation() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isAnonymous() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isClass() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isEnum() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isInterface() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isLocal() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isMember() throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public boolean isResolved() {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy loadTypeHierachy(InputStream arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newSupertypeHierarchy(ICompilationUnit[] arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newSupertypeHierarchy(IWorkingCopy[] arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newSupertypeHierarchy(WorkingCopyOwner arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newTypeHierarchy(IProgressMonitor arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newTypeHierarchy(IJavaProject arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newTypeHierarchy(ICompilationUnit[] arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newTypeHierarchy(IWorkingCopy[] arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newTypeHierarchy(WorkingCopyOwner arg0, IProgressMonitor arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public ITypeHierarchy newTypeHierarchy(IJavaProject arg0, WorkingCopyOwner arg1, IProgressMonitor arg2) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String[][] resolveType(String arg0) throws JavaModelException {
		throw new UnsupportedOperationException();
	}

	public String[][] resolveType(String arg0, WorkingCopyOwner arg1) throws JavaModelException {
		throw new UnsupportedOperationException();
	}
	
}