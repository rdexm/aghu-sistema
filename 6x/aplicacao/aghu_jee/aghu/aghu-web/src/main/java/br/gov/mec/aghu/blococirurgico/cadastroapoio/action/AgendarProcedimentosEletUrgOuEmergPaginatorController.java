package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.action.AgendaProcedimentosController;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PesquisaAgendarProcedimentosVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller para tela de pesquisa/cadastro de Areas de Triconomia.
 * 
 * @author dpacheco
 *
 */
public class AgendarProcedimentosEletUrgOuEmergPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 1735472780808433271L;

	private static final Log LOG = LogFactory.getLog(AgendarProcedimentosEletUrgOuEmergPaginatorController.class);

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<PesquisaAgendarProcedimentosVO> dataModel;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	
	private AghUnidadesFuncionais unidadeFuncional;
	private MbcSalaCirurgica salaCirurgica;
	private AghEspecialidades especialidade;
	private Date dataAgenda;

	@Inject
	private AgendaProcedimentosController agendaProcedimentosController;
	
	//Param de controle do dataTable
	
	private List<PesquisaAgendarProcedimentosVO> listaDisponibilidade;
	private Integer tamLista;
	
	private String paginaVoltar;
	
	// Integração #22463 disp.horário
	private Short dispHorarioUnfSeq, dispHorarioEspSeq, dispHorarioSalaSeq;
	private Long dispHorarioDataAgenda, dispHorarioHoraInicio;
	
	private static final String AGENDA_PROCEDIMENTOS = "blococirurgico-agendaProcedimentos";
	
	public void inicio(){
		this.especialidade = null;
		this.salaCirurgica = null;
		this.dataAgenda = null;
		this.unidadeFuncional = null;
		dataModel.setPesquisaAtiva(false);
		listaDisponibilidade= null;
		tamLista = null;

	}
	
	/**
	 * Navega para o XHTML de agendar procedimentos
	 * @param hrInicio
	 * @param sala
	 * @return
	 */
	public String agendarProcedimentos(Date hrInicio, Short sala) {
		agendaProcedimentosController.setDispHorarioUnfSeq(this.unidadeFuncional.getSeq());
		agendaProcedimentosController.setDispHorarioEspSeq(this.especialidade != null ? this.especialidade.getSeq() : 0);
		agendaProcedimentosController.setDispHorarioSalaSeq(sala);
		agendaProcedimentosController.setDispHorarioDataAgenda(this.dataAgenda.getTime());
		agendaProcedimentosController.setDispHorarioHoraInicio(hrInicio.getTime());
		return AGENDA_PROCEDIMENTOS;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String strPesquisa){
		return this.returnSGWithCount(aghuFacade.pesquisarUnidadesFuncionaisExecutoraCirurgias(strPesquisa, Boolean.TRUE, AghUnidadesFuncionais.Fields.DESCRICAO.toString()),pesquisarUnidadesFuncionaisCount(strPesquisa));
	}
	public Long pesquisarUnidadesFuncionaisCount(String strPesquisa){
		return aghuFacade.pesquisarUnidadesFuncionaisExecutoraCirurgiasCount(strPesquisa);
	}
	
	public List<MbcSalaCirurgica> pesquisarSalasCirurgicas(String strPesquisa){
		//Está ignorando o parametro de pesquisa enviado pela suggestion, considerando apenas o unf_seq selecionado anteriormente
		Short unfSeq 	= getUnidadeFuncional() != null ? getUnidadeFuncional().getSeq()  : null;
		Short seqPSala 	= !"".equals(strPesquisa) ? 	Short.valueOf(strPesquisa) : null;
		return this.returnSGWithCount(blocoCirurgicoCadastroApoioFacade.buscarSalaCirurgica(seqPSala, unfSeq, null, null, null, null, Boolean.TRUE, MbcSalaCirurgica.Fields.ID_SEQP),pesquisarSalasCirurgicasCount(strPesquisa));
	}
	public Long pesquisarSalasCirurgicasCount(String strPesquisa){
		Short unfSeq = getUnidadeFuncional() != null ? getUnidadeFuncional().getSeq() : null;
		String nome = !"".equals(strPesquisa) ? strPesquisa : null;
		return blocoCirurgicoCadastroApoioFacade.buscarSalaCirurgicaCount(null, unfSeq, nome, null, null, null);
	}
	
	public List<AghEspecialidades> pesquisarEspecialidades(String strPesquisa){
		return this.returnSGWithCount(aghuFacade.pesquisarEspecialidadesPorNomeOuSigla(strPesquisa != null ? strPesquisa : null),pesquisarEspecialidadesCount(strPesquisa));
	}
	public Long pesquisarEspecialidadesCount(String strPesquisa){
		return aghuFacade.pesquisarEspecialidadesPorNomeOuSiglaCount(strPesquisa != null ? strPesquisa : null);
	}
	
	public String getSimNao(Boolean simNao){
		return DominioSimNao.getInstance(simNao).getDescricao();
	}
	
	public void pesquisar(){
		listaDisponibilidade = null;
		tamLista = null;
		dataModel.reiniciarPaginator();
	}

	public String voltar() {
		return paginaVoltar;
	}
	
	private void buscarListaDisponibilidade() {
		try {
			listaDisponibilidade = blocoCirurgicoFacade.pesquisarMbcDisponibilidadeHorario(getUnidadeFuncional(), getSalaCirurgica(), getEspecialidade() , getDataAgenda());
			this.tamLista = listaDisponibilidade.size();
		} catch (ApplicationBusinessException e) {
			LOG.error("Excecao capturada: ", e);
		}
	}
	
	@Override
	public Long recuperarCount() {
		if(listaDisponibilidade == null || tamLista == null){
			this.buscarListaDisponibilidade();
		}
		return tamLista.longValue();
	}

	@Override
	public List<PesquisaAgendarProcedimentosVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		this.buscarListaDisponibilidade();
		if ((listaDisponibilidade != null) && !(listaDisponibilidade.isEmpty())){
			paginarLista(firstResult,maxResult,orderProperty, asc);
			return listaDisponibilidade;
		}
		return listaDisponibilidade;
	}
	
	private void paginarLista(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		if(tamLista == null || tamLista == 0){
			tamLista = listaDisponibilidade.size();
		}
		Integer lastResult = (firstResult + maxResult) > listaDisponibilidade.size() ? listaDisponibilidade.size() : (firstResult + maxResult);

		listaDisponibilidade = listaDisponibilidade.subList(firstResult, lastResult);
	}
	
	public void limpar(){
		setUnidadeFuncional(null);
		setSalaCirurgica(null);
		setEspecialidade(null);
		setDataAgenda(null);
		dataModel.setPesquisaAtiva(false);
		listaDisponibilidade= null;
		tamLista = null;
	}

	public void limparSalaCirurgica(){
		setSalaCirurgica(null);
	}
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public MbcSalaCirurgica getSalaCirurgica() {
		return salaCirurgica;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public Date getDataAgenda() {
		return dataAgenda;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public void setSalaCirurgica(MbcSalaCirurgica salaCirurgica) {
		this.salaCirurgica = salaCirurgica;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public void setDataAgenda(Date dataAgenda) {
		this.dataAgenda = dataAgenda;
	}

	public List<PesquisaAgendarProcedimentosVO> getListaDisponibilidade() {
		return listaDisponibilidade;
	}
	public Integer getTamLista() {
		return tamLista;
	}
	public void setListaDisponibilidade(
			List<PesquisaAgendarProcedimentosVO> listaDisponibilidade) {
		this.listaDisponibilidade = listaDisponibilidade;
	}
	public void setTamLista(Integer tamLista) {
		this.tamLista = tamLista;
	}
	public String getPaginaVoltar() {
		return paginaVoltar;
	}
	public void setPaginaVoltar(String paginaVoltar) {
		this.paginaVoltar = paginaVoltar;
	}

	public Short getDispHorarioUnfSeq() {
		return dispHorarioUnfSeq;
	}

	public void setDispHorarioUnfSeq(Short dispHorarioUnfSeq) {
		this.dispHorarioUnfSeq = dispHorarioUnfSeq;
	}

	public Short getDispHorarioEspSeq() {
		return dispHorarioEspSeq;
	}
	
	public void setDispHorarioEspSeq(Short dispHorarioEspSeq) {
		this.dispHorarioEspSeq = dispHorarioEspSeq;
	}
	
	public Short getDispHorarioSalaSeq() {
		return dispHorarioSalaSeq;
	}
	
	public void setDispHorarioSalaSeq(Short dispHorarioSalaSeq) {
		this.dispHorarioSalaSeq = dispHorarioSalaSeq;
	}

	public Long getDispHorarioDataAgenda() {
		return dispHorarioDataAgenda;
	}

	public void setDispHorarioDataAgenda(Long dispHorarioDataAgenda) {
		this.dispHorarioDataAgenda = dispHorarioDataAgenda;
	}

	public Long getDispHorarioHoraInicio() {
		return dispHorarioHoraInicio;
	}

	public void setDispHorarioHoraInicio(Long dispHorarioHoraInicio) {
		this.dispHorarioHoraInicio = dispHorarioHoraInicio;
	}

	public DynamicDataModel<PesquisaAgendarProcedimentosVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<PesquisaAgendarProcedimentosVO> dataModel) {
		this.dataModel = dataModel;
	}
	
}
