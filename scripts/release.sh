#!/bin/bash

github() {
    curl --location \
        https://api.github.com/repos/${github.repo.folder}/releases \
        --header "Content-Type: application/json" \
        --header "Authorization: bearer ${github.token}" \
        --data "$1"
}

create_tag() {
    git tag "v${project.version}"
    git push origin "v${project.version}"
}

create_release() {
    RELEASE_DATA=$( github '{
        "tag_name": "v${project.version}",
        "name": "Version ${project.version}",
        "draft": false,
        "prerelease": true
    }' )
    UPLOAD_URL_TEMPLATE=$( echo "$RELEASE_DATA" | jq -r '.upload_url' )
}

upload_file() {
    UPLOAD_BASE_URL="${UPLOAD_URL_TEMPLATE/\{*\}/}"
    UPLOAD_URL="${UPLOAD_BASE_URL}?name=${project.build.finalName}.jar"
    UPLOAD_DATA=$( curl --location \
        "${UPLOAD_URL}" \
        --header "Content-Type: application/java-archive" \
        --header "Authorization: bearer ${github.token}" \
        --data-binary "@${project.build.directory}/${project.build.finalName}.jar"
    )
    DOWNLOAD_URL=$( echo "$UPLOAD_DATA" | jq -r '.browser_download_url' )
}

push_update() {

    local WORKTREE="update-worktree"
    local BRANCH="${autoupdate.branch}"
    local FILE="${autoupdate.file}"

    cd "$( dirname "$0" )"
    if [ -d "$WORKTREE" ]
    then
        rm -fr "$WORKTREE"
    fi
    git clone \
        --no-tags \
        --branch "$BRANCH" \
        --single-branch \
        "${basedir}" \
        "${WORKTREE}"
    cp "$FILE" "${WORKTREE}"
    cd "${WORKTREE}"

    git add "$FILE"
    git commit -m "Set update file to version ${project.version}"
    git remote add github git@github.com:${github.repo.folder}.git
    git push origin "$BRANCH":"$BRANCH"
    git push github "$BRANCH":"$BRANCH"
}

run_command() {
    local CMD="$1"
    shift
    echo === "$@" =======================
    "$CMD" && echo --- OK /"$@" -------------------
    echo
}

from_src() {
    [ "${basedir}" == "" ]
}

generate_new_commit() {
    git checkout release
    git merge --no-commit master
    sed --in-place -e '
        /<version>/ {
            s_\(\s*<version\s*>\)\s*\(.*[^0-9]\)\([0-9]\+\)\s*\(</version>.*\)_@#@\1@#@\2@#@\3@#@\4@#@_
            h
            s/@#@.*@#@.*@#@\(.*\)@#@.*@#@/\1_/
        :next
            s/9_/_0/
            t next
            s/8_/9/
            s/7_/8/
            s/6_/7/
            s/5_/6/
            s/4_/5/
            s/3_/4/
            s/2_/3/
            s/1_/2/
            s/0_/1/
            s/^_/1/
            s/.*/@#@\0@#@/
            G
            s/@#@\(.*\)@#@.*@#@\(.*\)@#@\(.*\)@#@.*@#@\(.*\)@#@/\2\3\1\4/
            b printAll
        }
        b
        :printAll
        n
        b printAll
    ' pom.xml
    git add pom.xml
    VERSION=$(sed -n -e '
        /<version>/ {
            s_\(\s*<version\s*>\)\s*\(.*[^0-9]\)\([0-9]\+\)\s*\(</version>.*\)_\2\3_
            p
            q
        }
    ' 'pom.xml')
    git commit -m "Version ${VERSION}"
    mvn clean package
}

push_branches() {
    git push origin master release
}

halt_on_error() {
    set -e
}


halt_on_error

if from_src
then
    generate_new_commit
    push_branches
else
    run_command create_tag "Creating tag"
    run_command create_release "Creating release"
    run_command upload_file "Uploading plugin file"
    run_command push_update "Uploading update information"
fi
