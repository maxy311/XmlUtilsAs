#!/bin/bash

# 打印文件名称  git-for-all.sh
echo $0
# 打印传入参数数  fetct rebase 
echo $*

# must have git commands
if [ $# -eq 0 ] 
then
	echo USAGE: $0 git-command git-command-parameters ...
	exit 0
fi

ROOT=$(pwd)
gitCommand=$*
gitkStr="gitk"

call_git_command()
{
	echo ----------------[ $1 ]
	cd $1

	# gitcommand 是否包含 gitk
	if [[ $gitCommand =~ $gitkStr ]]
	then
  		$gitCommand
	else
  		git $gitCommand
	fi

	# git $input
	cd $ROOT	
}

# find all git projects paths
PROJECTS=$(find "/Users/maxy/Android/workspace/SHAREit" -maxdepth 3 -type d -name ".git" -a -not -path "./.repo/*" -print | sed 's/\/.git//')

# for each git project, do command
for P in $PROJECTS
do {
	call_git_command ${P:0}
}
done

echo ##########################################

