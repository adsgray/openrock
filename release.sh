#!/bin/sh

NAME="openrock"
VERSION="1.0_beta"

FILES="openrock.jar COPYING.txt README.txt bin/openrock bin/openrock.bat"
SRC_FILES="src gfx bin COPYING.txt README.txt TODO.txt release.sh .classpath .project openrock.jardesc"
DIRECTORY="${NAME}-${VERSION}"
ZIP_FILE="${NAME}-${VERSION}.zip"
TAR_FILE="${NAME}-${VERSION}.tar.gz"
SRC_TAR_FILE="${NAME}_src-${VERSION}.tar.gz"

echo "Release: ${NAME} ${VERSION}"

mkdir ${DIRECTORY}
cp ${FILES} ${DIRECTORY}

rm ${ZIP_FILE}

echo "Creating binary zip (for windows): ${ZIP_FILE}"
zip -r ${ZIP_FILE} ${DIRECTORY} -x \*.txt \*.bat
zip -r -l ${ZIP_FILE} ${DIRECTORY} -i \*.txt \*.bat

echo "Creating binary tar: ${TAR_FILE}"
tar -cvzf ${TAR_FILE} ${DIRECTORY}

rm ${DIRECTORY}/*

cp -r ${SRC_FILES} ${DIRECTORY}

echo "Creating source tar: ${SRC_TAR_FILE}"
tar -cvzf ${SRC_TAR_FILE} ${DIRECTORY} --exclude .svn

rm -rf ${DIRECTORY}


