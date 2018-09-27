class A {
    void foo() {
        try {
        } catch (IOException ioe) {
            throw new IOException(); // Noncompliant {{Catch clause must throw ServiceException}}
        }

        try {
        } catch (IOException ioe) {
            throw ioe; // Noncompliant {{Catch clause must throw ServiceException}}
        }
        try {
        } catch (IOException ioe) {
            throw ioe; // Noncompliant {{Catch clause must throw ServiceException}}
        }
        try {
        } catch (IOException ioe) {
            throw new ServiceException(ioe);
        }
        try {
        } catch (IOException ioe) { // Noncompliant {{Catch clause must throw ServiceException}}

        }
    }
}