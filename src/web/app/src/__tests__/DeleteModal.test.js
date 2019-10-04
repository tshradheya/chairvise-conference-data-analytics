import { shallowMount, createLocalVue} from '@vue/test-utils'
import DeleteModal from '@/components/common/DeleteModal.vue'

import Vue from 'vue'
import ElementUI from 'element-ui'

const localVue = createLocalVue();
localVue.use(ElementUI);


let component, mockDeleteFn
describe('DeleteModal.vue', () => {
  beforeEach(()=> {
    mockDeleteFn = jest.fn()
    component = shallowMount(DeleteModal, {
      propsData: {
        typeOfDelete: 'presentation',
        deleteFunction: mockDeleteFn
    }, localVue
    })
  })

  it('renders correct elements for presentation delete', () => {
    expect(component.find('span').exists()).toBe(true)
    expect(component.find('el-button-stub').classes()).toContain('delete')
    expect(component.find('el-button-stub').attributes('icon')).toBe("")
  })

  it('renders correct elements section delete', () => {
    component.setProps({
      typeOfDelete: 'section'
    })
    expect(component.props('typeOfDelete')).toBe('section')
    expect(component.find('el-button-stub').classes()).toContain('delete')
    expect(component.find('el-button-stub').attributes('icon')).toBe('el-icon-delete')
  })
})
