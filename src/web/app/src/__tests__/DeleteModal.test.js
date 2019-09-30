import { shallowMount } from '@vue/test-utils'
import DeleteModal from '@/components/common/DeleteModal.vue'

let component, mockDeleteFn
describe('DeleteModal.vue', () => {
  beforeEach(()=> {
    mockDeleteFn = jest.fn()
    component = shallowMount(DeleteModal, {
      propsData: {
        typeOfDelete: 'presentation',
        deleteFunction: mockDeleteFn
    }
    })
  })

  it('renders correct elements for presentation delete', () => {
    expect(component.find('span').exists()).toBe(true)
    expect(component.find('el-button').classes()).toContain('delete')
    expect(component.find('el-button').attributes('icon')).toBe(undefined)
  })

  it('renders correct elements section delete', () => {
    component.setProps({
      typeOfDelete: 'section'
    })
    expect(component.props('typeOfDelete')).toBe('section')
    expect(component.find('el-button').classes()).toContain('delete')
    expect(component.find('el-button').attributes('icon')).toBe('el-icon-delete')
  })
})
