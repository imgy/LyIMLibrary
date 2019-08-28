/*
 * Copyright (C) 2013 Sebastian Kaspari
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

package ani.internal
/**
 * @Author gaoyang
 * @Date 2019-08-28-09:46
 * @Email 329820506@qq.com
 *
 * 网络配置
 *
 */
internal object NetworkConfig {
    //TCP配置
    val TCP_DEFAULT_PORT = 4775

    //UDP配置
    val UDP_DEFAULT_MULTICAST_ADDRESS = "225.4.5.6"//225.0.0.0~238.255.255.255
    val UDP_DEFAULT_PORT = 5775
}
