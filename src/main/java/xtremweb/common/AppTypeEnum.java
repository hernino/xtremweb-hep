/*
 * Copyrights     : CNRS
 * Author         : Oleg Lodygensky
 * Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by inria : http://www.xtremweb.net/
 * Web            : http://www.xtremweb-hep.org
 *
 *      This file is part of XtremWeb-HEP.
 *
 *    XtremWeb-HEP is free software: you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *
 *    XtremWeb-HEP is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package xtremweb.common;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.AccessControlException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * This defines XtremWeb application type.
 *
 * Created: 16 novembre 2011
 *
 * @author <a href="mailto: lodygens at lal in2p3 fr">Oleg Lodygensky</a>
 * @since 8.0.0
 */

public enum AppTypeEnum {

	NONE,
	/**
	 * This denotes an application where workers must download executable This
	 * denotes application as known until XWHEP 7
	 */
	DEPLOYABLE,
	/**
	 * This denotes an application where workers don't download binary because
	 * the application is supposed to be pre-installed on workers. All following
	 * values in this enum represent shared application.
	 */
	SHARED,
	/**
	 * On July 3rd, 2017, this is our 2nd shared application: docker
	 * @since 11.0.0
	 */
	DOCKER {
		@Override
		public String getPathName() {
			return dockerpaths[OSEnum.getOs().ordinal()];
		}

		/**
		 * This retrieves application default pathname
		 *
		 * @return application binary path for the current OS
		 * @throws FileNotFoundException
		 *             if no application binary path found for the current OS
		 * @see xtremweb.common.OSEnum#getOs(String)
		 */
		@Override
		public File getPath() throws FileNotFoundException {
			final String filePath = dockerpaths[OSEnum.getOs().ordinal()];
			if (filePath == null) {
				throw new FileNotFoundException(NOBINPATH + this);
			}
			final File f = new File(filePath);
			if (f.exists()) {
				return f;
			}
			throw new FileNotFoundException(NOBINPATH + this);
		}
		/**
		 * This retrieves application default command line arguments
		 * @return "run"
		 * @since 12.1.0
		 */
		@Override
		public String getStartCommandLineArgs()  {
			return " run --rm ";
		}
		/**
		 * This retrieves command line arguments to mount PWD volume
		 * @param pwd represents the path of the present working directory
		 * @since 12.1.0
		 */
		@Override
		public String getMountVolumeCommandLine(final File pwd)  {
			return  " --mount type=bind,src=" + pwd.getAbsolutePath() + ",dst=" + pwd.getAbsolutePath();
		}
		/**
         * @see AppTypeEnum#checkParams(String)
         * @see #dockerForbiddenParamsSet
		 * @since 12.2.8
		 */
		@Override
		public void checkParams (final String params) throws AccessControlException {
			for(final String param : dockerForbiddenParamsSet){
                if(params.indexOf(param) > 0) {
                    throw new AccessControlException("Docker parameter forbidden : " + param);
                }
			}
		}
	},
	/**
	 * On Dec 2nd, 2011, this denotes our 1st shared application. This denotes
	 * VirtualBox as shared application
	 */
	VIRTUALBOX {
		@Override
		public String getPathName() {
			return virtualboxpaths[OSEnum.getOs().ordinal()];
		}

		/**
		 * This retrieves application default pathname
		 *
		 * @return application binary path for the current OS
		 * @throws FileNotFoundException
		 *             if no application binary path found for the current OS
		 * @see xtremweb.common.OSEnum#getOs(String)
		 * @since 8.0.0 (FG)
		 */
		@Override
		public File getPath() throws FileNotFoundException {
			final String filePath = virtualboxpaths[OSEnum.getOs().ordinal()];
			if (filePath == null) {
				throw new FileNotFoundException(NOBINPATH + this);
			}
			final File f = new File(filePath);
			if (f.exists()) {
				return f;
			}
			throw new FileNotFoundException(NOBINPATH + this);
		}
        /**
         * @see AppTypeEnum#checkParams(String)
         * @see #vboxForbiddenParamsSet
         * @since 12.2.8
         */
        @Override
        public void checkParams (final String params) throws AccessControlException {
            for(final String param : vboxForbiddenParamsSet){
                if(params.indexOf(param) > 0) {
                    throw new AccessControlException("Docker parameter forbidden : " + param);
                }
            }
        }
	};

	public static final AppTypeEnum LAST = VIRTUALBOX;
	public static final int SIZE = LAST.ordinal() + 1;

	/**
	 * This contains default docker pathnames (one entry per OS).
	 * Each entry is a semicolon separated paths list
	 * paths
	 *
	 * @since 11.0.0
	 */
	private static final String[] dockerpaths = { null, // NONE
			"/usr/bin/docker", // LINUX
			"c:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe", // WIN32
			"/usr/local/bin/docker", // MACOSX
			null, // SOLARIS
			null // JAVA
	};
	/**
	 * This contains forbidden Docker command line parameters
	 *
	 * @since 12.2.8
	 */
	private static final String[] dockerForbiddenParams = {
			"--add-host",
			"--cap-add",
			"--cgroup-parent",
			"--cidfile",
			"--cpu-count",
			"--cpu-percent",
			"--cpu-shares",
			"-c",
			"--cpus",
			"-v",
			"--volume",
			"--volume-driver",
			"--volumes-from",
			"--privileged",
			"--entrypoint",
			"--device",
			"-i",
			"-ti",
			"-t",
			"--tty"
	};
    /**
     * This contains forbidden Docker command line parameters set
     *
     * @since 12.2.8
     */
    public static final Set<String> dockerForbiddenParamsSet = new HashSet<String>(Arrays.asList(dockerForbiddenParams));
    /**
     * This contains forbidden VirtualBox command line parameters
     *
     * @since 12.2.8
     */
    private static final String[] vboxForbiddenParams = {
            "-s",
            "-startvm",
            "--startvm",
            "-v",
            "-vrde",
            "--vrde",
            "-e",
            "-vrdeproperty",
            "--vrdeproperty",
            "--settingspw",
            "--settingspwfile",
            "-start-paused",
            "--start-paused",
            "-c",
            "-capture",
            "--capture",
            "-w",
            "--width",
            "-h",
            "--height",
            "-r",
            "--bitrate",
            "-f",
            "--filename"
};
    /**
     * This contains forbidden VirtualBox command line parameters set
     *
     * @since 12.2.8
     */
    public static final Set<String> vboxForbiddenParamsSet = new HashSet<String>(Arrays.asList(vboxForbiddenParams));

	/**
	 * This array stores default VirtualBox pathnames (one entry per OS). Each
	 * entry is a semicolon separated paths list Defaults are Oracle VirtualBox
	 * paths
	 *
	 * @since 8.0.0 (FG)
	 */
	private static final String[] virtualboxpaths = { null, // NONE
			"/usr/bin/VBoxHeadless", // LINUX
			"c:\\Program Files\\Oracle\\VirtualBox\\VBoxHeadless.exe;c:\\Program Files\\VirtualBox\\VBoxHeadless.exe;c:\\Program Files (x86)\\Oracle\\VirtualBox\\VBoxHeadless.exe;c:\\Program Files (x86)\\VirtualBox\\VBoxHeadless.exe", // WIN32
			"/Applications/VirtualBox.app/Contents/MacOS/VBoxHeadless", // MACOSX
			null, // SOLARIS
			null // JAVA
	};

	private static final String NOBINPATH = "no binary path for ";

	/**
	 * This retrieves an OS from its ordinal value
	 *
	 * @exception IllegalArgumentException
	 *                is thrown if v is not a valid ordinal value
	 */
	public static AppTypeEnum fromInt(final int v) throws IllegalArgumentException {
		for (final AppTypeEnum i : AppTypeEnum.values()) {
			if (i.ordinal() == v) {
				return i;
			}
		}
		throw new IllegalArgumentException("unvalid XWOSes value " + v);
	}

	/**
	 * This retrieves this string representation
	 *
	 * @return a array containing this string representation
	 */
	public static String[] getLabels() {
		final String[] labels = new String[SIZE];
		for (final AppTypeEnum c : AppTypeEnum.values()) {
			labels[c.ordinal()] = c.toString();
		}
		return labels;
	}

	/**
	 * This retrieves application default pathname
	 * @return application binary path if available
	 * @throws FileNotFoundException
	 *             if no binary path available for the provided application type
	 * @see xtremweb.common.OSEnum#getOs(String)
	 * @since 8.0.0 (FG)
	 */
	public String getPathName() {
		return null;
	}

	/**
     * This checks command line parameters to avoid forbidden parameters.
	 * This does nor throws nothing; should be overridden
	 * @param params
	 *            is a String containing command line parameters
     * @throws AccessControlException if params contains at least one forbidden parameter
	 * @since 12.2.8
	 */
	public void checkParams (final String params) throws AccessControlException {
	}

	/**
	 * This retrieves application default pathname
     * If not overridden, this always throws a FileNotFoundException
	 * @return application binary path for the current OS
	 * @throws FileNotFoundException
	 *             if no application binary path found for the current OS
	 * @see xtremweb.common.OSEnum#getOs(String)
	 * @since 8.0.0 (FG)
	 */
	public File getPath() throws FileNotFoundException {
		throw new FileNotFoundException(NOBINPATH + this);
	}
	/**
	 * This retrieves application default command line arguments
	 * @return an empty string
	 * @since 12.1.0
	 */
	public String getStartCommandLineArgs() {
		return "";
	}
	/**
	 * This retrieves command line arguments to mount PWD volume
	 * @param pwd represents the path of the present working directory
	 * @return an empty string
	 * @since 12.1.0
	 */
	public String getMountVolumeCommandLine(final File pwd) {
		return "";
	}
	/**
	 * This dumps path
	 *
	 * @see xtremweb.common.OSEnum#getOs(String)
	 * @since 8.0.0 (FG)
	 */
	public static void dumpPath() {
		for (final AppTypeEnum a : AppTypeEnum.values()) {
			try {
				System.out.println(a.getPath());
			} catch (final Exception e) {
			}
		}
	}

	/**
	 * This checks if the provided application is available
	 *
	 * @return true if getPath() != null; false otherwise
	 * @throws FileNotFoundException
	 *             if application path is not valid
	 * @see #getPath()
	 * @since 8.0.0 (FG)
	 */
	public boolean available() throws FileNotFoundException {
		return getPath() != null;
	}

	/**
	 * This is for debug purposes
	 *
	 * @param argv
	 */
	public static void main(final String[] argv) {
		for (final AppTypeEnum i : AppTypeEnum.values()) {
			System.out.println(i.toString());
		}
	}

}
