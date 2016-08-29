package br.gov.mec.aghu.emergencia.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.configuracao.vo.Especialidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.internacao.vo.UnidadeFuncional;
import br.gov.mec.aghu.model.MamProtClassifRisco;
import br.gov.mec.aghu.model.MamUnidAtendeEsp;
import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.locator.ServiceLocator;

/**
 * Classe responsável por controlar as ações do criação e edição de
 * tipo de unidade funcional.
 */
public class UnidadeFuncionalEmergenciaController  extends ActionController implements ActionPaginator{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1408451175566950412L;
	
	
	private IEmergenciaFacade emergenciaFacade = ServiceLocator.getBean(IEmergenciaFacade.class, "aghu-emergencia");
	
	@EJB
	private IAghuFacade aghuFacade;

	private MamUnidAtendem mamUnidAtendem;
	private UnidadeFuncional unidadeFuncional;
	private MamProtClassifRisco mamProtClassifRisco;
	private Boolean indSituacao;
	private Boolean indObrOrgPaciente;
	
	/*** DETALHE - CAMPOS ESPECIALIDADE ***/
	private Especialidade especialidade;
	private Date horaInicioMarcaCons;
	private Date horaFimMarcaCons;
	private Boolean indSituacaoEsp;
	private Boolean indMarcaExtra;	
	private Boolean indSoMarcaConsDia;
	private Boolean indResponsavelMenor;
	
	private final String PAGE_LIST_UNIDADE = "unidadeFuncionalEmergenciaList";
	
	@Inject @Paginator
	private DynamicDataModel<MamUnidAtendeEsp> dataModel;

	private MamUnidAtendeEsp mamUnidAtendeEsp;
	
	private Boolean bloqueiaUnidadeFuncional;
	private Boolean bloqueiaEspecialidade;
	
	@PostConstruct
	public void init() {
		begin(conversation);	
		
	}
	
	public void inicio(){
		if (this.getMamUnidAtendem() != null && this.getMamUnidAtendem().getUnfSeq() != null) {
			List<UnidadeFuncional> listaUnid = this.emergenciaFacade.pesquisarUnidadeFuncional(this.getMamUnidAtendem().getUnfSeq().toString());
			if (listaUnid != null && listaUnid.size() > 0) {
			    setUnidadeFuncional(listaUnid.get(0));
			}
			this.dataModel.reiniciarPaginator();					
			
			setIndSituacao(this.getMamUnidAtendem().getIndSituacao() != null && 
	                       this.getMamUnidAtendem().getIndSituacao().equals(DominioSituacao.A));			
			setBloqueiaUnidadeFuncional(Boolean.TRUE);
			setMamProtClassifRisco(this.getMamUnidAtendem().getMamProtClassifRisco());
			setIndResponsavelMenor(this.getMamUnidAtendem().getIndMenorResponsavel());
			setIndObrOrgPaciente(this.getMamUnidAtendem().getIndObrOrgPaciente());
		}
		else {
			 this.setMamUnidAtendem(new MamUnidAtendem());
			 setUnidadeFuncional(null);		
			 setIndSituacao(Boolean.TRUE);
			 setBloqueiaUnidadeFuncional(Boolean.FALSE);
			 setIndResponsavelMenor(Boolean.FALSE);
			 setMamProtClassifRisco(null);
		}	
		this.limparUnidAtendeEsp();
		
	}
	
	/**
	 * Método que realiza a ação do botão confirmar na tela de cadastro de
	 * tipo de unidade funcional
	 */
	public void confirmar() {
		String hostName = null;
		try {
			hostName = this.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR,
					"Não foi possivel pegar o servidor logado");
		}
		
		try {
			boolean create = this.getMamUnidAtendem().getUnfSeq() == null;			

			mamUnidAtendem.setIndSituacao(DominioSituacao.getInstance(this.indSituacao));
			mamUnidAtendem.setMamProtClassifRisco(this.mamProtClassifRisco);
			mamUnidAtendem.setIndObrOrgPaciente(this.indObrOrgPaciente);
			mamUnidAtendem.setIndMenorResponsavel(this.indResponsavelMenor);
			if (create) {
				
				mamUnidAtendem.setUnfSeq(this.unidadeFuncional.getSeq());
				mamUnidAtendem.setAghUnidadesFuncionais(this.aghuFacade.obterAghUnidFuncionaisPorUnfSeq(this.unidadeFuncional.getSeq()));
				this.emergenciaFacade.inserir(mamUnidAtendem, hostName);
				
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CADASTRO_UNIDADE");
			} else {
				
				this.emergenciaFacade.alterar(getMamUnidAtendem(), hostName);
				
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_UNIDADE");				
				
			}
			this.dataModel.reiniciarPaginator();
			this.setBloqueiaUnidadeFuncional(Boolean.TRUE);

		} catch (ApplicationBusinessException e) {	
			mamUnidAtendem.setUnfSeq(null);	
			apresentarExcecaoNegocio(e);
		}		
		
	}
	
	
	@Override
	public Long recuperarCount() {
		return emergenciaFacade.pesquisarUnidAtendeEspPorUnidadeAtendemCount(getMamUnidAtendem().getUnfSeq());		
	}

	
	@Override
	public List<MamUnidAtendeEsp> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return emergenciaFacade.pesquisarUnidAtendeEspPorUnidadeAtendem(firstResult, maxResult, orderProperty, asc, getMamUnidAtendem().getUnfSeq());		
	}
	
	/***** Ações responsaveis por salvar, editar, excluir especialidades vinculada a unidade ****/
	
	public void gravarUnidAtendeEsp() {

		String hostName = null;
		try {
			hostName = this.getEnderecoIPv4HostRemoto().getHostName();
		} catch (UnknownHostException e) {
			apresentarMsgNegocio(Severity.ERROR,
					"Não foi possivel pegar o servidor logado");
		}

		try {
			boolean create = this.getMamUnidAtendeEsp() == null
					|| this.getMamUnidAtendeEsp().getId() == null;

			if (create) {
				mamUnidAtendeEsp = new MamUnidAtendeEsp();
				mamUnidAtendeEsp.setMamUnidAtendem(getMamUnidAtendem());
				mamUnidAtendeEsp.setAghEspecialidades(aghuFacade.obterAghEspecialidadesPorChavePrimaria(this.getEspecialidade().getSeq()));
			}

			
			mamUnidAtendeEsp.setIndSituacao(DominioSituacao.getInstance(this
					.getIndSituacaoEsp()));
			mamUnidAtendeEsp.setIndMarcaExtra(this.getIndMarcaExtra());
			mamUnidAtendeEsp.setIndSoMarcaConsDia(this.getIndSoMarcaConsDia());
			mamUnidAtendeEsp.setHoraInicioMarcaCons(this
					.getHoraInicioMarcaCons());
			mamUnidAtendeEsp.setHoraFimMarcaCons(this.getHoraFimMarcaCons());

			if (create) {
				this.emergenciaFacade.inserirUnidAtendeEsp(mamUnidAtendeEsp,
						hostName);
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CADASTRO_ESPECIALIDADE_UNIDADE");
			} else {
				this.emergenciaFacade.alterarUnidAtendeEsp(mamUnidAtendeEsp,
						hostName);
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_ESPECIALIDADE_UNIDADE");

			}

			this.limparUnidAtendeEsp();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}
	
	public void editarUnidAtendeEsp() {
		if (this.getMamUnidAtendeEsp() != null) {
			this.setEspecialidade(this.emergenciaFacade.obterEspecialidade(this
					.getMamUnidAtendeEsp().getAghEspecialidades().getSeq()));
			this.setHoraInicioMarcaCons(this.getMamUnidAtendeEsp()
					.getHoraInicioMarcaCons());
			this.setHoraFimMarcaCons(this.getMamUnidAtendeEsp()
					.getHoraFimMarcaCons());
			this.setIndMarcaExtra(this.getMamUnidAtendeEsp().getIndMarcaExtra());
			this.setIndSoMarcaConsDia(this.getMamUnidAtendeEsp()
					.getIndSoMarcaConsDia());
			this.setIndSituacaoEsp(this.getMamUnidAtendeEsp().getIndSituacao() != null
					&& this.getMamUnidAtendeEsp().getIndSituacao()
							.equals(DominioSituacao.A));
			this.setBloqueiaEspecialidade(true);
		}

	}

	public void excluirUnidAtendeEsp() {
		if (mamUnidAtendeEsp.getId() != null) {
			this.emergenciaFacade.excluirUnidAtendeEsp(mamUnidAtendeEsp.getId());
			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_EXCLUSAO_ESPECIALIDADE");
			this.limparUnidAtendeEsp();
		}
		this.dataModel.reiniciarPaginator();
	}

	public void limparUnidAtendeEsp() {
		this.setMamUnidAtendeEsp(null);
		this.setEspecialidade(null);
		this.setHoraInicioMarcaCons(null);
		this.setHoraFimMarcaCons(null);
		this.setIndMarcaExtra(true);
		this.setIndSoMarcaConsDia(true);
		this.setIndSituacaoEsp(true);
		this.setBloqueiaEspecialidade(false);

	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * tipo de unidade funcional
	 */
	public String cancelar() {	
		this.setMamUnidAtendem(new MamUnidAtendem());	
		this.getMamUnidAtendem().setUnfSeq(null);
		setUnidadeFuncional(null);		
		setIndSituacao(Boolean.TRUE);
		return PAGE_LIST_UNIDADE;
	}	
	
	public List<UnidadeFuncional> pesquisarUnidadeFuncional(Object objPesquisa) {
		return this.emergenciaFacade.pesquisarUnidadeFuncional(objPesquisa);
	}
	
	public List<Especialidade> pesquisarEspecialidade(String objPesquisa) {
		return this.emergenciaFacade.pesquisarEspecialidade(objPesquisa);
	}

	public List<MamProtClassifRisco> pesquisarProtocolosClassificacao(String objPesquisa){
		return  this.returnSGWithCount(emergenciaFacade.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricao(objPesquisa, 100),pesquisarProtocolosClassificacaoCount(objPesquisa));
	}
	
	public Long pesquisarProtocolosClassificacaoCount(String objPesquisa){
		return emergenciaFacade.pesquisarProtocolosClassificacaoRiscoAtivosPorCodigoDescricaoCount(objPesquisa);
	}
	
	public Boolean getBolIndSituacao(DominioSituacao indSituacao){		
		return indSituacao != null && indSituacao.equals(DominioSituacao.A);		
	}
	
	public Especialidade obterEspecialidade(Short seq){
	   return this.emergenciaFacade.obterEspecialidade(seq);	
	}
	
	// ### GETs e SETs ###
	

	public MamUnidAtendem getMamUnidAtendem() {
		return mamUnidAtendem;
	}

	public void setMamUnidAtendem(MamUnidAtendem mamUnidAtendem) {
		this.mamUnidAtendem = mamUnidAtendem;
	}

	public UnidadeFuncional getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(UnidadeFuncional unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	public Boolean getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(Boolean indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Especialidade getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(Especialidade especialidade) {
		this.especialidade = especialidade;
	}

	public DynamicDataModel<MamUnidAtendeEsp> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamUnidAtendeEsp> dataModel) {
		this.dataModel = dataModel;
	}

	public MamUnidAtendeEsp getMamUnidAtendeEsp() {
		return mamUnidAtendeEsp;
	}

	public void setMamUnidAtendeEsp(MamUnidAtendeEsp mamUnidAtendeEsp) {
		this.mamUnidAtendeEsp = mamUnidAtendeEsp;
	}

	public Date getHoraInicioMarcaCons() {
		return horaInicioMarcaCons;
	}

	public void setHoraInicioMarcaCons(Date horaInicioMarcaCons) {
		this.horaInicioMarcaCons = horaInicioMarcaCons;
	}

	public Date getHoraFimMarcaCons() {
		return horaFimMarcaCons;
	}

	public void setHoraFimMarcaCons(Date horaFimMarcaCons) {
		this.horaFimMarcaCons = horaFimMarcaCons;
	}

	public Boolean getIndSituacaoEsp() {
		return indSituacaoEsp;
	}

	public void setIndSituacaoEsp(Boolean indSituacaoEsp) {
		this.indSituacaoEsp = indSituacaoEsp;
	}

	public Boolean getIndMarcaExtra() {
		return indMarcaExtra;
	}

	public void setIndMarcaExtra(Boolean indMarcaExtra) {
		this.indMarcaExtra = indMarcaExtra;
	}

	public Boolean getIndSoMarcaConsDia() {
		return indSoMarcaConsDia;
	}

	public void setIndSoMarcaConsDia(Boolean indSoMarcaConsDia) {
		this.indSoMarcaConsDia = indSoMarcaConsDia;
	}

	public Boolean getBloqueiaUnidadeFuncional() {
		return bloqueiaUnidadeFuncional;
	}

	public void setBloqueiaUnidadeFuncional(Boolean bloqueiaUnidadeFuncional) {
		this.bloqueiaUnidadeFuncional = bloqueiaUnidadeFuncional;
	}

	public Boolean getBloqueiaEspecialidade() {
		return bloqueiaEspecialidade;
	}

	public void setBloqueiaEspecialidade(Boolean bloqueiaEspecialidade) {
		this.bloqueiaEspecialidade = bloqueiaEspecialidade;
	}

	public MamProtClassifRisco getMamProtClassifRisco() {
		return mamProtClassifRisco;
	}

	public void setMamProtClassifRisco(MamProtClassifRisco mamProtClassifRisco) {
		this.mamProtClassifRisco = mamProtClassifRisco;
	}

	public Boolean getIndObrOrgPaciente() {
		return indObrOrgPaciente;
	}

	public void setIndObrOrgPaciente(Boolean indObrOrgPaciente) {
		this.indObrOrgPaciente = indObrOrgPaciente;
	}

	public Boolean getIndResponsavelMenor() {
		return indResponsavelMenor;
	}

	public void setIndResponsavelMenor(Boolean indResponsavelMenor) {
		this.indResponsavelMenor = indResponsavelMenor;
	}
	
}
