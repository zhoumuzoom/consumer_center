import React, { useEffect, useMemo, useState } from 'react'
import { AppstoreOutlined, ApartmentOutlined, CloudUploadOutlined, DashboardOutlined, DeploymentUnitOutlined, MenuFoldOutlined, MenuUnfoldOutlined, LogoutOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons'
import { Avatar, Breadcrumb, Button, Dropdown, Layout, Space, theme } from 'antd'
import { ProLayout } from '@ant-design/pro-components'
import { Outlet, useLocation, useNavigate } from '@umijs/max'
import { useLocale } from '../locales'
import { useAuth } from '../hooks/useAuth'
import LangSwitcher from '../components/LangSwitcher'

const { Content } = Layout

export default function BasicLayout() {
  const { token } = theme.useToken()
  const { t } = useLocale()
  const location = useLocation()
  const navigate = useNavigate()
  const auth = useAuth()
  const [collapsed, setCollapsed] = useState(false)

  useEffect(() => {
    if (!auth.isLoggedIn) navigate(`/login?redirect=${encodeURIComponent(location.pathname)}`, { replace: true })
    else if (!auth.userInfo) auth.fetchUserInfo().catch(() => null)
  }, [auth.isLoggedIn, location.pathname])

  const menuItems = useMemo(() => [
    { path: '/dashboard', icon: <DashboardOutlined />, name: t('menu.dashboard') },
    { path: '/customers', icon: <UserOutlined />, name: t('menu.customers') },
    { path: '/customer-tree', icon: <ApartmentOutlined />, name: t('menu.customerTree') },
    { path: '/industry-tree', icon: <AppstoreOutlined />, name: t('menu.industryTree') },
    { path: '/decision-makers', icon: <TeamOutlined />, name: t('menu.decisionMakers') },
    { path: '/decision-chains', icon: <DeploymentUnitOutlined />, name: t('menu.decisionChains') },
    { path: '/group-report', icon: <CloudUploadOutlined />, name: t('menu.groupReport') }
  ], [t])

  if (!auth.isLoggedIn) return null
  const title = menuItems.find((item) => location.pathname.startsWith(item.path))?.name || t('common.page')
  return (
    <ProLayout
      title={t('app.name')}
      logo={<div className="logo-icon">C</div>}
      collapsed={collapsed}
      onCollapse={setCollapsed}
      location={{ pathname: location.pathname }}
      menu={{ request: async () => menuItems }}
      menuItemRender={(item, dom) => <a onClick={() => navigate(item.path)}>{dom}</a>}
      headerRender={() => (
        <div className="app-header">
          <Space>
            <Button type="text" icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />} onClick={() => setCollapsed(!collapsed)} />
            <Breadcrumb items={[{ title: t('common.home'), onClick: () => navigate('/') }, { title }]} />
          </Space>
          <Space>
            <LangSwitcher showLabel />
            <Dropdown menu={{ items: [{ key: 'logout', icon: <LogoutOutlined />, label: t('common.logout') }], onClick: ({ key }) => key === 'logout' && auth.logout().then(() => navigate('/login')) }}>
              <Button type="text"><Avatar size="small" icon={<UserOutlined />} /> {auth.displayName || t('common.currentUser')}</Button>
            </Dropdown>
          </Space>
        </div>
      )}
      token={{ colorPrimary: token.colorPrimary }}
    >
      <Content className="app-content"><Outlet /></Content>
    </ProLayout>
  )
}
