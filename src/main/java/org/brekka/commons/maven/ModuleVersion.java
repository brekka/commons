/*
 * Copyright 2011 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.brekka.commons.maven;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Utility class that will retrieve the version of a Maven module as identified by its group and artifact ids.
 * 
 * @author Andrew Taylor (andrew@brekka.org)
 */
public final class ModuleVersion implements Serializable {
    
    
    /**
     * Generates a timestamp of the format "yyyyMMddHHmm".
     */
    public static final NotFoundVersion TIMESTAMP = new NotFoundVersion() {
        @Override
        public String getVersion() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
            return sdf.format(new Date());
        }
    };
    
    /**
     *  Logger
     */
    private static final Log log = LogFactory.getLog(ModuleVersion.class);
    
    /**
     * Serial UID
     */
    private static final long serialVersionUID = -4918649895174535509L;
    
    
    private final String groupId;
    private final String artifactId;
    private final String version;
    
    /**
     * Utility non-constructor
     */
    private ModuleVersion(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }
    
    public String getGroupId() {
        return groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public String getVersion() {
        return version;
    }



    /**
     * Retrieve the version for the specified module
     * 
     * @param groupId the id of the group
     * @param artifactId the id of the artifact
     * @param classLoader the loader containing the module to resolve the version for
     */
    public static ModuleVersion getVersion(String groupId, String artifactId, ClassLoader classLoader) {
        NotFoundVersion nfv = new NotFoundThrows(groupId, artifactId);
        return getVersion(groupId, artifactId, classLoader, nfv);
    }
    
    
    /**
     * @param groupId the id of the group
     * @param artifactId the id of the artifact
     * @param classLoader the loader containing the module to resolve the version for
     * @param notFoundString the string to return should the module information not be resolvable
     */
    public static ModuleVersion getVersion(String groupId, String artifactId, ClassLoader classLoader, NotFoundVersion noFoundVersion) {
        String pomPropsPath = preparePath(groupId, artifactId);
        String version = null;
        try {
            version = getVersion(classLoader.getResourceAsStream(pomPropsPath));
        } catch (IOException e) {
            // Something went wrong
            if (log.isWarnEnabled()) {
                log.warn(String.format(
                        "Failed to determine version of Maven module with groupId '%s' and artifactId '%s' from classpath",
                        groupId, artifactId));
            }
        }
        if (version == null) {
            version = noFoundVersion.getVersion();
        }
        return new ModuleVersion(groupId, artifactId, version);
    }
    
    /**
     * @param groupId the id of the group
     * @param artifactId the id of the artifact
     * @param servletContext the servlet context of the application containing the module to resolve the version for
     * @param notFoundString the string to return should the module information not be resolvable
     */
    public static ModuleVersion getVersion(String groupId, String artifactId, ServletContext servletContext, NotFoundVersion noFoundVersion) {
        String pomPropsPath = preparePath(groupId, artifactId);
        String version = null;
        try {
            version = getVersion(servletContext.getResourceAsStream(pomPropsPath));
        } catch (IOException e) {
            // Something went wrong
            if (log.isWarnEnabled()) {
                log.warn(String.format(
                        "Failed to determine version of Maven module with groupId '%s' and artifactId '%s' from classpath",
                        groupId, artifactId));
            }
        }
        if (version == null) {
            version = noFoundVersion.getVersion();
        }
        return new ModuleVersion(groupId, artifactId, version);
    }
    
    private static String getVersion(InputStream is) throws IOException {
        String version = null;
        try {
            if (is != null) {
                Properties pomProps = new Properties();
                pomProps.load(is);
                version = pomProps.getProperty("version");
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                // Ignore
                if (log.isDebugEnabled()) {
                    log.debug("Error closing stream", e);
                }
            }
        }
        return version;
    }

    /**
     * @param groupId
     * @param artifactId
     * @return
     */
    private static String preparePath(String groupId, String artifactId) {
        String pomPropsPath = String.format("META-INF/maven/%s/%s/pom.properties", groupId, artifactId);
        return pomPropsPath;
    }

    
    /**
     * In the event that a module cannot be resolved, an implementation of this interface can be
     * used to generate a string that should be returned in its place.
     */
    public interface NotFoundVersion {
        String getVersion();
    }
    
    /**
     * Simply returns the specified string if no version can be found for the module
     */
    public static class NotFoundString implements NotFoundVersion {
        private final String notFoundString;
        public NotFoundString(String notFoundString) {
            this.notFoundString = notFoundString;
        }
        @Override
        public String getVersion() {
            return notFoundString;
        }
    }
    
    /**
     * Simply returns the specified string if no version can be found for the module
     */
    private static class NotFoundThrows implements NotFoundVersion {
        private final String groupId;
        private final String artifactId;
        public NotFoundThrows(String groupId, String artifactId) {
            this.groupId = groupId;
            this.artifactId = artifactId;
        }
        @Override
        public String getVersion() {
            throw new IllegalStateException(String.format(
                    "Unable to locate version for Maven module with groupId '%s' and artifactId '%s'",
                    groupId, artifactId));
        }
    }
}
