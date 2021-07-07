package life.qbic.portal.sampletracking.system


import life.qbic.datamodel.dtos.portal.PortalUser

/**
 * <p>Utility class that provides access to some testing system context properties.</p>
 *
 * @since 1.0.0
 */
class TestingSystemContext {

    /**
     * <p>Provides dummy user information, if the application runs in testing mode.</p>
     *
     * <p>Requires the system environment variables <b>EMAIL</b> and <b>AUTH_ID</b> to be set.
     *
     * @return the logged in {@link life.qbic.datamodel.dtos.portal.PortalUser}.
     * @since 1.0.0
     */
    static Optional<PortalUser> getUser() {
        String email = System.getProperty("email")
        String auth_id = System.getProperty("auth_id")
        PortalUser testUser = new PortalUser.Builder(email,
                auth_id, "Test", "User", email).build()
        return Optional.of(testUser)
    }

}
