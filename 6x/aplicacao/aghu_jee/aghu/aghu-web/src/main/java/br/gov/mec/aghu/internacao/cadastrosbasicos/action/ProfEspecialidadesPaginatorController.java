package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.RapServidoresEspecialidadesVO;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ProfEspecialidadesPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -1265920286647222608L;
	
	private static final String PAGE_CADASTRAR_PROF_ESPECIALIDADES = "profEspecialidadesCRUD";

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private RapServidores rapServidores;
	private RapServidoresEspecialidadesVO servidorSelecionado;
	@Inject @Paginator
	private DynamicDataModel<RapServidoresEspecialidadesVO> dataModel;	
	private Integer matriculaPesquisaProfEspecialidades;
	private Integer codigoVinculoPesquisaProfEspecialidades;
	private String nomePesquisaProfEspecialidades;
	private List<AghProfEspecialidades> listaAghProfEspecialidades;
	
	private String voltarPara;

	@PostConstruct
	public void init(){
		begin(conversation, true);
		this.matriculaPesquisaProfEspecialidades = null;
		this.codigoVinculoPesquisaProfEspecialidades = null;
		this.nomePesquisaProfEspecialidades = null;
	}
	
	/**

	 * <p>Método invocado ao acessar a tela</p>

	 * <p>Adicionado durante a implementação da estoria #8674</p>

	 * @author rafael.silvestre

	 */

	public void iniciar() {
		if (matriculaPesquisaProfEspecialidades != null && 
				codigoVinculoPesquisaProfEspecialidades != null &&
				nomePesquisaProfEspecialidades != null) {
//			servidorSelecionado = this.registroColaboradorFacade.obterRapServidoresPorChavePrimaria(new RapServidoresId(serMatricula, serVinCodigo));
			pesquisar();
		}
	}

	
	private static final Comparator<AghProfEspecialidades> COMPARATOR_PROFISSIONAL_ESPECIALIDADES = new Comparator<AghProfEspecialidades>() {
		@Override
		public int compare(AghProfEspecialidades o1, AghProfEspecialidades o2) {
			return o1.getAghEspecialidade().getSigla().toUpperCase().compareTo(
					o2.getAghEspecialidade().getSigla().toUpperCase());
		}
	};

	public void carregarProfissional(RapServidoresEspecialidadesVO rapVO) {
		this.rapServidores = registroColaboradorFacade.obterServidor(rapVO.getVinCodigo(), rapVO.getMatricula());
		this.listaAghProfEspecialidades = this.cadastrosBasicosInternacaoFacade.listarProfEspecialidadesPorServidor(rapServidores);
		Collections.sort(this.listaAghProfEspecialidades, COMPARATOR_PROFISSIONAL_ESPECIALIDADES);
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();	
	}

	public void limparPesquisa() {
		this.matriculaPesquisaProfEspecialidades = null;
		this.codigoVinculoPesquisaProfEspecialidades = null;
		this.nomePesquisaProfEspecialidades = null;
		this.dataModel.limparPesquisa();
	}
	
	public String editar(){
		begin(conversation);
		return PAGE_CADASTRAR_PROF_ESPECIALIDADES;
	}

	@Override
	public Long recuperarCount() {
		return cadastrosBasicosInternacaoFacade.pesquisarRapServidoresCount(matriculaPesquisaProfEspecialidades, codigoVinculoPesquisaProfEspecialidades, nomePesquisaProfEspecialidades);
	}

	@Override
	public List<RapServidores> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {

		return this.cadastrosBasicosInternacaoFacade
				.pesquisarRapServidores(firstResult, maxResults, orderProperty,
						asc, matriculaPesquisaProfEspecialidades,
						codigoVinculoPesquisaProfEspecialidades,
						nomePesquisaProfEspecialidades);
	}
	
	/**
	 * <p>Ação do botão Voltar</p>
	 * <p>Adicionado durante a implementação da estoria #8674</p>
	 * @author rafael.silvestre
	 */
	public String voltar() {
		return voltarPara;
	}
	
	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	public Integer getMatriculaPesquisaProfEspecialidades() {
		return matriculaPesquisaProfEspecialidades;
	}

	public void setMatriculaPesquisaProfEspecialidades(
			Integer matriculaPesquisaProfEspecialidades) {
		this.matriculaPesquisaProfEspecialidades = matriculaPesquisaProfEspecialidades;
	}

	public Integer getCodigoVinculoPesquisaProfEspecialidades() {
		return codigoVinculoPesquisaProfEspecialidades;
	}

	public void setCodigoVinculoPesquisaProfEspecialidades(
			Integer codigoVinculoPesquisaProfEspecialidades) {
		this.codigoVinculoPesquisaProfEspecialidades = codigoVinculoPesquisaProfEspecialidades;
	}

	public String getNomePesquisaProfEspecialidades() {
		return nomePesquisaProfEspecialidades;
	}

	public void setNomePesquisaProfEspecialidades(
			String nomePesquisaProfEspecialidades) {
		this.nomePesquisaProfEspecialidades = nomePesquisaProfEspecialidades;
	}

	public List<AghProfEspecialidades> getListaAghProfEspecialidades() {
		return listaAghProfEspecialidades;
	}

	public void setListaAghProfEspecialidades(
			List<AghProfEspecialidades> listaAghProfEspecialidades) {
		this.listaAghProfEspecialidades = listaAghProfEspecialidades;
	}

	public DynamicDataModel<RapServidoresEspecialidadesVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<RapServidoresEspecialidadesVO> dataModel) {
		this.dataModel = dataModel;
	}

	public RapServidoresEspecialidadesVO getServidorSelecionado() {
		return servidorSelecionado;
	}

	public void setServidorSelecionado(RapServidoresEspecialidadesVO servidorSelecionado) {
		this.servidorSelecionado = servidorSelecionado;
	}
}
