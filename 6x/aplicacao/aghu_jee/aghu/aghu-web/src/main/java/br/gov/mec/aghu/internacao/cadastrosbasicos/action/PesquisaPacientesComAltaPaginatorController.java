package br.gov.mec.aghu.internacao.cadastrosbasicos.action;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaPacComAlta;
import br.gov.mec.aghu.dominio.DominioTipoAlta;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.VAinAltasVO;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinObservacoesPacAlta;
import br.gov.mec.aghu.model.AinTiposAltaMedica;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;



public class PesquisaPacientesComAltaPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9018625039699629884L;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
		
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IInternacaoFacade internacaoFacade;

	private Date dataInicial;
	private Date dataFinal;
	private boolean altaAdministrativa;
	private DominioTipoAlta tipoAlta;
	private DominioOrdenacaoPesquisaPacComAlta ordenacao;
	private AghUnidadesFuncionais unidadeFuncional;
	private AghEspecialidades especialidade;
	private AinTiposAltaMedica tipoAltaMedica;
	private AinObservacoesPacAlta observacoesPacAlta;
	private VAinAltasVO voAltaPaciente;
	private VAinAltasVO voAltaPacienteSelecionada;
	@Inject @Paginator
	private DynamicDataModel<VAinAltasVO> dataModel;
	
	
	private static final Log LOG = LogFactory.getLog(PesquisaPacientesComAltaPaginatorController.class);
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
		this.dataInicial = new Date();
		this.dataFinal = new Date();
		this.altaAdministrativa = true;
		this.tipoAlta = DominioTipoAlta.T;
		this.ordenacao= DominioOrdenacaoPesquisaPacComAlta.U;
		this.voAltaPaciente = new VAinAltasVO();
	}


	/**
	 * Limpa os campos da tela.
	 */
	public void limparPesquisa() {
		this.dataInicial = new Date();
		this.dataFinal = new Date();
		this.altaAdministrativa = true;
		this.tipoAlta = DominioTipoAlta.T;
		this.ordenacao= DominioOrdenacaoPesquisaPacComAlta.U;
		this.unidadeFuncional = null;
		this.especialidade = null;
		this.tipoAltaMedica = null;
		this.voAltaPaciente = new VAinAltasVO();
		this.dataModel.limparPesquisa();
		//this.ativo = false;
	}

	/**
	 * Método que realiza a ação do botão pesquisar.
	 */
	public void pesquisar() {
		try{
			this.pesquisaInternacaoFacade.validaDefineWhere(this.dataInicial, this.dataFinal, this.altaAdministrativa, this.tipoAlta,  this.tipoAltaMedica != null? this.tipoAltaMedica.getCodigo(): null);
			this.dataModel.reiniciarPaginator();
		}catch(ApplicationBusinessException e){
			apresentarExcecaoNegocio(e);
			dataModel.limparPesquisa();
		}
	}

	
	@Override
	public Long recuperarCount() {
		return this.pesquisaInternacaoFacade.pesquisaPacientesComAltaCount(this.dataInicial, this.dataFinal, this.altaAdministrativa, this.tipoAlta,
				this.unidadeFuncional != null? this.unidadeFuncional.getSeq(): null, this.especialidade != null? this.especialidade.getSeq(): null,
						this.tipoAltaMedica != null? this.tipoAltaMedica.getCodigo(): null);
	}
	
	

	@Override
	public List<VAinAltasVO> recuperarListaPaginada(Integer firstResult,	Integer maxResult, String orderProperty, boolean asc) {
		List<VAinAltasVO> lista = this.pesquisaInternacaoFacade.pesquisaPacientesComAlta(firstResult, maxResult,
				orderProperty, asc, this.dataInicial ,this.dataFinal, this.altaAdministrativa, this.tipoAlta, this.ordenacao,
				this.unidadeFuncional != null? this.unidadeFuncional.getSeq(): null, this.especialidade != null? this.especialidade.getSeq(): null,
						this.tipoAltaMedica != null? this.tipoAltaMedica.getCodigo(): null);
		
		for (Iterator iterator = lista.iterator(); iterator.hasNext();) {
			VAinAltasVO vAinAltasVO = (VAinAltasVO) iterator.next();
			vAinAltasVO.setMatricula(this.pesquisaInternacaoFacade.buscaMatriculaConvenio(vAinAltasVO.getProntuario(), vAinAltasVO.getCspCnvCodigo(),vAinAltasVO.getCspSeq()));
			vAinAltasVO.setSenha(this.pesquisaInternacaoFacade.buscaSenhaInternacao(vAinAltasVO.getSeqInternacao()));
			vAinAltasVO.setLocal(this.pesquisaInternacaoFacade.montaLocalAltaDePaciente(vAinAltasVO.getLtoLtoId(), vAinAltasVO.getQrtNumero(), vAinAltasVO.getAndar(), vAinAltasVO.getIndAla()));
			vAinAltasVO.setEquipe(this.pesquisaInternacaoFacade.buscarNomeUsual(vAinAltasVO.getVinCodigoServidorProfessor(), vAinAltasVO.getMatriculaServidorProfessor()));
		}
		
		return lista; 
	}
	
	//Metodos para a suggestion de tipos de alta medica
	public List<AinTiposAltaMedica> pesquisarTipoAltaMedicaPorCodigoEDescricao(String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarTipoAltaMedicaPorCodigoEDescricao((String)param);
	}


	//Metodos para a suggestion de unidades funcionais
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(
			String param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoDescricaoCaractInternacaoOuEmergenciaAtivasInativas(param);
	}

	//Metodos para a suggestion de especialidades
	public List<AghEspecialidades> listarEspecialidadesPorSigla(String paramPesquisa) {
		return this.aghuFacade.listarPorSiglaAtivas(paramPesquisa);
	}
	
	public void editar(VAinAltasVO voSelecionado) {
		this.setVoAltaPaciente(voSelecionado);
		if (this.voAltaPaciente.getCodigoPacienteAlta() == null) {
			this.observacoesPacAlta = null;
		} else {
			this.observacoesPacAlta = new AinObservacoesPacAlta();		
			this.observacoesPacAlta.setCodigo(this.voAltaPaciente.getCodigoPacienteAlta());
			this.observacoesPacAlta.setDescricao(this.voAltaPaciente.getObservacaoPacienteAlta());
		}
	}
	
	public void associarObservacao() {
		try {
			Integer seqInternacao = this.voAltaPaciente.getSeqInternacao();
			Integer codigoObservacaoPacAlta = null;
			String descricaoPacAlta = null;

			if (this.observacoesPacAlta != null) {
				codigoObservacaoPacAlta = this.observacoesPacAlta.getCodigo();
				descricaoPacAlta = this.observacoesPacAlta.getDescricao();
			}
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoIPv4HostRemoto().toString();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			this.internacaoFacade.associarObservacaoPacAlta(seqInternacao, codigoObservacaoPacAlta, nomeMicrocomputador, new Date());

			// Atualiza o código e descrição da observação selecionada, para evitar
			// a necessidade de refazer a pesquisa da grid.
			this.voAltaPaciente.setCodigoPacienteAlta(codigoObservacaoPacAlta);
			this.voAltaPaciente.setObservacaoPacienteAlta(descricaoPacAlta);
			this.dataModel.reiniciarPaginator();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ATUALIZAR_OBSERVACAO_ALTA");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		 
	}
	
	/**
	 * Método que retorna uma coleção de Observações de Alta de Paicente 
	 * p/ preencher a suggestion box, de acordo com filtro informado.
	 * 
	 * @param parametro
	 * @return
	 */	
	public List<AinObservacoesPacAlta> pesquisarObservacoesPacAlta(String parametro) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarObservacoesPacAlta(parametro);
	}	
	
	//GETs e SETs
	public Date getDataInicial() {
		return this.dataInicial;
	}


	public void setDataInicial(Date dataInicial) {
		Calendar periodoInicio = Calendar.getInstance();
		
		periodoInicio.setTime(dataInicial);
		periodoInicio.set(Calendar.HOUR_OF_DAY, 0);
		periodoInicio.set(Calendar.MINUTE, 0);
		periodoInicio.set(Calendar.SECOND, 0);
		periodoInicio.set(Calendar.MILLISECOND, 0);
		
		this.dataInicial = periodoInicio.getTime();
	}


	public Date getDataFinal() {
		return this.dataFinal;
	}


	public void setDataFinal(Date dataFinal) {
		Calendar periodoFim = Calendar.getInstance();
		
		periodoFim.setTime(dataFinal);
		periodoFim.set(Calendar.HOUR_OF_DAY, 23);
		periodoFim.set(Calendar.MINUTE, 59);
		periodoFim.set(Calendar.SECOND, 59);
		periodoFim.set(Calendar.MILLISECOND, 999);

		this.dataFinal = periodoFim.getTime();
	}


	public boolean isAltaAdministrativa() {
		return this.altaAdministrativa;
	}


	public void setAltaAdministrativa(boolean altaAdministrativa) {
		this.altaAdministrativa = altaAdministrativa;
	}


	public DominioTipoAlta getTipoAlta() {
		return this.tipoAlta;
	}


	public void setTipoAlta(DominioTipoAlta tipoAlta) {
		this.tipoAlta = tipoAlta;
	}

	public DominioOrdenacaoPesquisaPacComAlta getOrdenacao() {
		return this.ordenacao;
	}


	public void setOrdenacao(DominioOrdenacaoPesquisaPacComAlta ordenacao) {
		this.ordenacao = ordenacao;
	}


	public AghUnidadesFuncionais getUnidadeFuncional() {
		return this.unidadeFuncional;
	}


	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}


	public AghEspecialidades getEspecialidade() {
		return this.especialidade;
	}


	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}


	public AinTiposAltaMedica getTipoAltaMedica() {
		return this.tipoAltaMedica;
	}


	public void setTipoAltaMedica(AinTiposAltaMedica tipoAltaMedica) {
		this.tipoAltaMedica = tipoAltaMedica;
	}

	public void setVoAltaPaciente(VAinAltasVO voAltaPaciente) {
		this.voAltaPaciente = voAltaPaciente;
	}

	public VAinAltasVO getVoAltaPaciente() {
		return this.voAltaPaciente;
	}


	public void setObservacoesPacAlta(AinObservacoesPacAlta observacoesPacAlta) {
		this.observacoesPacAlta = observacoesPacAlta;
	}


	public AinObservacoesPacAlta getObservacoesPacAlta() {
		return this.observacoesPacAlta;
	}


	public VAinAltasVO getVoAltaPacienteSelecionada() {
		return voAltaPacienteSelecionada;
	}


	public void setVoAltaPacienteSelecionada(VAinAltasVO voAltaPacienteSelecionada) {
		this.voAltaPacienteSelecionada = voAltaPacienteSelecionada;
	}


	public DynamicDataModel<VAinAltasVO> getDataModel() {
		return dataModel;
	}


	public void setDataModel(DynamicDataModel<VAinAltasVO> dataModel) {
		this.dataModel = dataModel;
	}

}