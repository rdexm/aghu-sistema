package br.gov.mec.aghu.ambulatorio.action;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.ProfissionalHospitalVO;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapVinculos;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaProfissionaisHospitalPaginatorController extends ActionController implements ActionPaginator {

        /**
         * 
         */
        private static final long serialVersionUID = 3893365864631117858L;
        private static final String PESQUISAR_EQUIPES_DE_PROFISSIONAIS = "internacao-manterEquipesPorProfissionalList";
        private static final String PESQUISAR_ESPECIALIDADES_PARA_PROFISSIONAIS = "internacao-profEspecialidadesList";
        private static final String PESQUISAR_CONVENIOS_DO_PROFISSIONAL = "internacao-profConveniosList";
        private static final String MENSAGEM_CAMPOS_OBRIGATORIOS = "MENSAGEM_CAMPOS_OBRIGATORIOS_PESQUISAR_PROFISSIONAIS";

        @EJB
        private IRegistroColaboradorFacade registroColaboradorFacade;
        @EJB
        private IAmbulatorioFacade ambulatorioFacade;
        private RapServidores profissional;
        private RapVinculos vinculo;
        private RapConselhosProfissionais conselho;
        private ProfissionalHospitalVO profissionalSelecionado;

        @Inject 
        @Paginator
        private DynamicDataModel<ProfissionalHospitalVO> dataModel;

        

        @PostConstruct
        protected void inicializar() {
                begin(conversation);
        }

        

        /**
         * Ação do botão Pesquisar
         */

        public void pesquisar() {

                if (profissional != null || vinculo != null || conselho != null) {

                        this.dataModel.reiniciarPaginator();

                } else {

                        apresentarMsgNegocio(Severity.WARN, MENSAGEM_CAMPOS_OBRIGATORIOS);

                }

        }

        

        /**

         * Ação do botão Limpar

         */

        public void limpar() {

                Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();

                while (componentes.hasNext()) {

                        limparValoresSubmetidos(componentes.next());

                }

                this.profissional = null;

                this.vinculo = null;

                this.conselho = null;

                this.dataModel.limparPesquisa();

        }

        

        /**

         * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)

         * 

         * @param object {@link Object}

         */

        private void limparValoresSubmetidos(Object object) {

                if (object == null || object instanceof UIComponent == false) {

                        return;

                }

                Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();

                while (uiComponent.hasNext()) {

                        limparValoresSubmetidos(uiComponent.next());

                }

                if (object instanceof UIInput) {

                        ((UIInput) object).resetValue();

                }

        }

        

        @Override
        public List<ProfissionalHospitalVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

                return this.ambulatorioFacade.obterListaProfissionaisHospital(this.profissional, this.vinculo, this.conselho, 

                                firstResult, maxResult, orderProperty, asc);

        }

        @Override
        public Long recuperarCount() {

                return this.ambulatorioFacade.obterCountProfissionaisHospital(this.profissional, this.vinculo, this.conselho);

        }

        

        /**

         * Suggestion Box de Profissional.

         */

        public List<RapServidores> listarProfissional(String parametro) {

                return returnSGWithCount(this.registroColaboradorFacade.obterListaProfissionalPorMatriculaVinculoOuNome(parametro),

                                                                 this.registroColaboradorFacade.obterCountProfissionalPorMatriculaVinculoOuNome(parametro));

        }

        

        /**

         * Suggestion Box de Vinculo.

         */

        public List<RapVinculos> listarVinculo(String parametro) {

                return returnSGWithCount(this.registroColaboradorFacade.obterListaVinculoPorCodigoOuDescricao(parametro),

                                                                 this.registroColaboradorFacade.obterCountVinculoPorCodigoOuDescricao(parametro));

        }

        

        /**

         * Suggestion Box de Conselho.

         */

        public List<RapConselhosProfissionais> listarConselho(String parametro) {

                return returnSGWithCount(this.registroColaboradorFacade.obterListaConselhoPorCodigoOuSigla(parametro),

                                                                 this.registroColaboradorFacade.obterCountConselhoPorCodigoOuSigla(parametro));

        }

        

        /**

         * Redireciona para a tela da estoria #8424 

         */

        public String pesquisarEquipesProfissionais() {

                return PESQUISAR_EQUIPES_DE_PROFISSIONAIS;

        }

        

        /**

         * Redireciona para a tela da estoria #1197

         */

        public String pesquisarEspecialidadesProfissionais() {

                return PESQUISAR_ESPECIALIDADES_PARA_PROFISSIONAIS;

        }

        

        /**

         * Redireciona para a tela da estoria #1644

         */

        public String pesquisarConveniosProfissional() {

                return PESQUISAR_CONVENIOS_DO_PROFISSIONAL;

        }

        /**

         * 

         * Gets and Sets

         * 

         */

        public RapServidores getProfissional() {

                return profissional;

        }

        public void setProfissional(RapServidores profissional) {

                this.profissional = profissional;

        }

        public RapVinculos getVinculo() {

                return vinculo;

        }

        public void setVinculo(RapVinculos vinculo) {

                this.vinculo = vinculo;

        }

        public RapConselhosProfissionais getConselho() {
                return conselho;
        }

        public void setConselho(RapConselhosProfissionais conselho) {
                this.conselho = conselho;
        }

        public DynamicDataModel<ProfissionalHospitalVO> getDataModel() {
                return dataModel;
        }

        public void setDataModel(DynamicDataModel<ProfissionalHospitalVO> dataModel) {
                this.dataModel = dataModel;
        }
        
    	public ProfissionalHospitalVO getProfissionalSelecionado() {
    		return profissionalSelecionado;
    	}

    	public void setProfissionalSelecionado(ProfissionalHospitalVO profissionalSelecionado) {
    		this.profissionalSelecionado = profissionalSelecionado;
    	}


}
