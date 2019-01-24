#!/bin/sh
#
# Copyright 2019 Institut Laue–Langevin
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


if [ -z "$PUMA_RESOLVER_VM_ARGS" ]; then
	export PUMA_RESOLVER_VM_ARGS="$PUMA_RESOLVER_VM_ARGS_DEFAULT"
	echo "PUMA_RESOLVER_VM_ARGS set to $PUMA_RESOLVER_VM_ARGS"
else
	echo "PUMA_RESOLVER_VM_ARGS defined as $PUMA_RESOLVER_VM_ARGS"
fi

# Run the application
java $PUMA_RESOLVER_VM_ARGS -jar target/puma-url-resolver.jar server url-resolver.yml
