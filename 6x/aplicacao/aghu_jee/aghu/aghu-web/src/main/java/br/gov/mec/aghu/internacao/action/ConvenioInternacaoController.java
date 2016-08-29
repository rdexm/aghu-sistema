package br.gov.mec.aghu.internacao.action;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.persistence.OptimisticLockException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class ConvenioInternacaoController extends ActionController {

	private static final long serialVersionUID = 3606325134957693305L;

	private static final Log LOG = LogFactory.getLog(ConvenioInternacaoController.class);

	private static final String PAGE_CADASTRO_INTERNACAO = "cadastroInternacao";

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;
	
	@Inject
	private CadastroInternacaoController cadastroInternacaoController;

	private AinInternacao internacao;

	private AipPacientes paciente;

	private Short aghUniFuncSeq;

	private String ainLeitoId;

	private String quartoDescricao;

	private Integer seqContaHosp;

	private String strDataIntAdministrativa;

	private FatConvenioSaudePlano convenioSaudePlano;

	private ConvenioPlanoVO convenioPlanoVO;

	/**
	 * Seq da Internação, obtido via page parameter.
	 */
	private Integer ainInternacaoSeq;

	/**
	 * Código do Paciente, obtido via page parameter.
	 */
	private Integer aipPacCodigo;

	List<FatContasInternacao> listaContasInternacao = new ArrayList<FatContasInternacao>();

	private Date dataAlteracaoConvenio;

	private Short convenioId;

	private Byte planoId;

	private Date dataAlterada;

	private FatContasInternacao contaInternacao = new FatContasInternacao();

	private Boolean showModal;
	
	private FatContasHospitalares contaHospitalarOld;
	
	private String dataOldStr=null;
	
	private Date dataOld = null;
	
	private String dataAlteracaoStr=null;
	
	private Date dataAlteracao = null;
	
	
	private enum ConvenioInternacaoExceptionCode implements BusinessExceptionCode {
		MENSAGEM_ERRO_CONTA_JA_COBRADA, DATA_HORA_ANTERIOR_DATA_HORA_INTERNACAO_ADMINISTRATIVA
	}
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 


//		this.internacao = internacaoFacade.obterInternacaoPorSeq(this.ainInternacaoSeq, AinInternacao.Fields.PACIENTE);
//		this.paciente = internacao.getPaciente();

		convenioPlanoVO = internacaoFacade.obterConvenioPlanoVO(internacao.getConvenioSaudePlano().getId().getCnvCodigo(), internacao
				.getConvenioSaudePlano().getId().getSeq());
		
		

		if (internacao.getLeito() != null) {
			ainLeitoId = internacao.getLeito().getLeitoID();
		} else if (internacao.getQuarto() != null) {
			internacao.setQuarto(internacaoFacade.obterAinQuartosPorChavePrimaria(internacao.getQuarto().getNumero()));
			quartoDescricao = internacao.getQuarto().getDescricao();
		} else {
			aghUniFuncSeq = internacao.getUnidadesFuncionais().getSeq();
		}

		// List<FatContasInternacao> listaContasRefresh =
		// faturamentoFacade.pesquisarContasInternacaoOrderDtInternacaoDesc(internacao.getSeq());
		// for (FatContasInternacao item : listaContasRefresh) {
		// // TODO AJUSTAR
		// // internacaoFacade.refresh(item);// TODO AJUSTAR
		// }

		this.setListaContasInternacao(faturamentoFacade.pesquisarContasInternacaoOrderDtInternacaoDesc(internacao.getSeq()));

		List<FatContasInternacao> listaContasInternacao = this.getListaContasInternacao();
		if (!listaContasInternacao.isEmpty()) {
			this.contaInternacao = listaContasInternacao.get(0);
		}
	
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de atualização de
	 * acomodação autorizada.
	 */
	public String confirmar() {

		try {
			if (convenioSaudePlano != null) {
				internacao.setConvenioSaudePlano(convenioSaudePlano);
				internacao.setConvenioSaude(convenioSaudePlano.getConvenioSaude());
				FatContasHospitalares contaHospitalar = new FatContasHospitalares();
				contaHospitalar.setDataInternacaoAdministrativa(dataAlteracaoConvenio);
				contaHospitalar.setConvenioSaudePlano(convenioSaudePlano);
				contaHospitalar.setContaManuseada(false);
				this.obterContaHospitalarAnterior();
		
				if (contaHospitalarOld==null ||(contaHospitalarOld!=null && dataOld.after(dataAlteracao))) {
					throw new ApplicationBusinessException(ConvenioInternacaoExceptionCode.DATA_HORA_ANTERIOR_DATA_HORA_INTERNACAO_ADMINISTRATIVA);
				}
				
				if (contaHospitalarOld!=null && contaHospitalarOld.getIndSituacao().equals(DominioSituacaoConta.O)) {
					this.internacao = pesquisaInternacaoFacade.obterInternacao(this.internacao.getSeq());
					throw new ApplicationBusinessException(ConvenioInternacaoExceptionCode.MENSAGEM_ERRO_CONTA_JA_COBRADA);
				}
				
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção caputada:", e);
				}

				final Date dataFimVinculoServidor = new Date();
				if(contaHospitalarOld!=null && dataOldStr.compareTo(dataAlteracaoStr)==0){
					internacao = internacaoFacade.alterarConvenioInternacao(internacao, contaHospitalar, contaHospitalarOld.getSeq(), nomeMicrocomputador,
							dataFimVinculoServidor, true);
				} else {
			  		
					internacaoFacade.alterarConvenioInternacao(internacao, contaHospitalar, contaHospitalarOld.getSeq(), nomeMicrocomputador,dataFimVinculoServidor, false);
				}
				

				this.setListaContasInternacao(faturamentoFacade.pesquisarContasInternacaoOrderDtInternacaoDesc(internacao.getSeq()));

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CONVENIO_PLANO_SALVO");

				this.convenioSaudePlano = null;
				this.dataAlteracaoConvenio = null;
				this.convenioId = null;
				this.planoId = null;
				
				this.cadastroInternacaoController.setInternacao(internacao);

				return PAGE_CADASTRO_INTERNACAO;
			} else {
				apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SELECIONAR_CONVENIO_PLANO");
			}

		} catch (BaseException e) {
			this.showModal = false;
			this.internacao = pesquisaInternacaoFacade.obterInternacao(this.internacao.getSeq());
			apresentarExcecaoNegocio(e);
		} catch (OptimisticLockException e) {
			LOG.error(e.getMessage(), e);
			throw e;
		} 
		
		return null;
	}
	
	private void obterContaHospitalarAnterior(){
		try{
		List<FatContasInternacao> listaContasInternacao = faturamentoFacade
				.pesquisarContasInternacaoOrderDtInternacaoDesc(internacao.getSeq());

		FatContasHospitalares contaHospitalarAtual;
		
		
		Date dataAdm = null;
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		dataAlteracaoStr = dateFormat.format(dataAlteracaoConvenio);
		dataAlteracao = dateFormat.parse(dataAlteracaoStr);
		
		String dataAdmStr = null;
			
		for (FatContasInternacao conta : listaContasInternacao) {
			dataAdmStr = dateFormat.format(conta.getContaHospitalar().getDataInternacaoAdministrativa());
			dataAdm = dateFormat.parse(dataAdmStr);
			if(dataAdm.equals(dataAlteracao)){
				contaHospitalarOld = faturamentoFacade.obterContaHospitalar(conta.getContaHospitalar().getSeq(),
						FatContasHospitalares.Fields.SERVIDOR_MANUSEADO);
				dataOldStr = dateFormat.format(contaHospitalarOld.getDataInternacaoAdministrativa());
				dataOld = dateFormat.parse(dataOldStr);
				break;
			}

			if(dataAdm.before(dataAlteracao)){
				contaHospitalarOld = faturamentoFacade.obterContaHospitalar(conta.getContaHospitalar().getSeq(),
						FatContasHospitalares.Fields.SERVIDOR_MANUSEADO);
				dataOldStr = dateFormat.format(contaHospitalarOld.getDataInternacaoAdministrativa());
				dataOld = dateFormat.parse(dataOldStr);
				break;
			}
		}
		
		
		for (FatContasInternacao conta : listaContasInternacao) {
			contaHospitalarAtual = faturamentoFacade.obterContaHospitalar(conta.getContaHospitalar().getSeq(),
					FatContasHospitalares.Fields.SERVIDOR_MANUSEADO);
			dataAdmStr = dateFormat.format(contaHospitalarAtual.getDataInternacaoAdministrativa());
			dataAdm = dateFormat.parse(dataAdmStr);
			if(contaHospitalarAtual.getDtAltaAdministrativa()==null){
				contaHospitalarOld = contaHospitalarAtual;
				dataOldStr = dateFormat.format(contaHospitalarOld.getDataInternacaoAdministrativa());
				dataOld = dateFormat.parse(dataOldStr);
				break;
			}
			if(contaHospitalarOld!=null && dataAdm.after(dataOld) && (dataAdm.before(dataAlteracao)||dataAdm.equals(dataAlteracao))){
				contaHospitalarOld = contaHospitalarAtual;
				dataOldStr = dateFormat.format(contaHospitalarOld.getDataInternacaoAdministrativa());
				dataOld = dateFormat.parse(dataOldStr);
			}
		}
		}
		
		catch(ParseException e){
			LOG.error(e.getMessage(), e);
		}

	}

	public void buscarDataInicioInternacao() {
		if (contaInternacao != null && contaInternacao.getContaHospitalar() != null) {
			this.dataAlteracaoConvenio = contaInternacao.getContaHospitalar().getDataInternacaoAdministrativa();
		} else {
			this.dataAlteracaoConvenio = internacao.getDthrInternacao();
		}

	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de atualização de
	 * acomodação autorizada.
	 */
	public String cancelar() {

		this.convenioSaudePlano = null;

		this.convenioSaudePlano = null;
		this.dataAlteracaoConvenio = null;
		this.convenioId = null;
		this.planoId = null;

		return PAGE_CADASTRO_INTERNACAO;
	}

	// Getters and Setters

	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Integer getAinInternacaoSeq() {
		return ainInternacaoSeq;
	}

	public void setAinInternacaoSeq(Integer ainInternacaoSeq) {
		this.ainInternacaoSeq = ainInternacaoSeq;
	}

	public Integer getAipPacCodigo() {
		return aipPacCodigo;
	}

	public void setAipPacCodigo(Integer aipPacCodigo) {
		this.aipPacCodigo = aipPacCodigo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getStyleProntuario() {
		String retorno = "";

		if (paciente != null && paciente.isProntuarioVirtual()) {
			retorno = "background-color:#0000ff";
		}
		return retorno;

	}

	public Short getAghUniFuncSeq() {
		return aghUniFuncSeq;
	}

	public void setAghUniFuncSeq(Short aghUniFuncSeq) {
		this.aghUniFuncSeq = aghUniFuncSeq;
	}

	public String getAinLeitoId() {
		return ainLeitoId;
	}

	public void setAinLeitoId(String ainLeitoId) {
		this.ainLeitoId = ainLeitoId;
	}

	public Integer getSeqContaHosp() {
		return seqContaHosp;
	}

	public void setSeqContaHosp(Integer seqContaHosp) {
		this.seqContaHosp = seqContaHosp;
	}

	public String getStrDataIntAdministrativa() {
		return strDataIntAdministrativa;
	}

	public void setStrDataIntAdministrativa(String strDataIntAdministrativa) {
		this.strDataIntAdministrativa = strDataIntAdministrativa;
	}

	public ConvenioPlanoVO getConvenioPlanoVO() {
		return convenioPlanoVO;
	}

	public void setConvenioPlanoVO(ConvenioPlanoVO convenioPlanoVO) {
		this.convenioPlanoVO = convenioPlanoVO;
	}

	/**
	 * @return the listaContasInternacao
	 */
	public List<FatContasInternacao> getListaContasInternacao() {
		return listaContasInternacao;
	}

	/**
	 * @param listaContasInternacao
	 *            the listaContasInternacao to set
	 */
	public void setListaContasInternacao(List<FatContasInternacao> listaContasInternacao) {
		this.listaContasInternacao = listaContasInternacao;
	}

	/**
	 * @return the convenioSaudePlano
	 */
	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	/**
	 * @param convenioSaudePlano
	 *            the convenioSaudePlano to set
	 */
	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	/**
	 * @return the dataAlteracaoConvenio
	 */
	public Date getDataAlteracaoConvenio() {
		return dataAlteracaoConvenio;
	}

	public Date atribuirDataAlteracaoConvenio() {
		return this.dataAlteracaoConvenio;
	}

	/**
	 * @param dataAlteracaoConvenio
	 *            the dataAlteracaoConvenio to set
	 */
	public void setDataAlteracaoConvenio(Date dataAlteracaoConvenio) {
		this.dataAlteracaoConvenio = dataAlteracaoConvenio;
	}

	/**
	 * @return the convenioId
	 */
	public Short getConvenioId() {
		return convenioId;
	}

	/**
	 * @param convenioId
	 *            the convenioId to set
	 */
	public void setConvenioId(Short convenioId) {
		this.convenioId = convenioId;
	}

	/**
	 * @return the planoId
	 */
	public Byte getPlanoId() {
		return planoId;
	}

	/**
	 * @param planoId
	 *            the planoId to set
	 */
	public void setPlanoId(Byte planoId) {
		this.planoId = planoId;
	}

	/**
	 * 
	 */
	public void escolherPlanoConvenio() {
		if (this.planoId != null && this.convenioId != null) {
			FatConvenioSaudePlano plano = this.faturamentoApoioFacade.obterPlanoPorIdConvenioInternacao(this.planoId, this.convenioId);
			if (plano == null) {
				apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO", this.convenioId, this.planoId);
			}
			this.atribuirPlano(plano);
		}
	}

	public void atribuirPlano(FatConvenioSaudePlano plano) {
		if (plano != null) {
			this.convenioSaudePlano = plano;
			this.convenioId = plano.getConvenioSaude().getCodigo();
			this.planoId = plano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public void atribuirPlano() {
		if (this.convenioSaudePlano != null) {
			this.convenioId = this.convenioSaudePlano.getConvenioSaude().getCodigo();
			this.planoId = this.convenioSaudePlano.getId().getSeq();
		} else {
			this.convenioSaudePlano = null;
			this.convenioId = null;
			this.planoId = null;
		}
	}

	public List<FatConvenioSaudePlano> pesquisarAlteraConvenioSaudePlanos(String parametro) {
		return this.faturamentoApoioFacade.pesquisarConvenioSaudePlanosInternacao((String) parametro);
	}

	/**
	 * @return the contaInternacao
	 */
	public FatContasInternacao getContaInternacao() {
		return contaInternacao;
	}

	/**
	 * @param contaInternacao
	 *            the contaInternacao to set
	 */
	public void setContaInternacao(FatContasInternacao contaInternacao) {
		this.contaInternacao = contaInternacao;
	}

	/**
	 * @return the dataAlterada
	 */
	public Date getDataAlterada() {
		return dataAlterada;
	}

	/**
	 * @param dataAlterada
	 *            the dataAlterada to set
	 */
	public void setDataAlterada(Date dataAlterada) {
		this.dataAlterada = dataAlterada;
	}

	public void apresentaModal() {
		this.showModal = true;
	}

	/**
	 * @return the showModal
	 */
	public Boolean getShowModal() {
		return showModal;
	}

	/**
	 * @param showModal
	 *            the showModal to set
	 */
	public void setShowModal(Boolean showModal) {
		this.showModal = showModal;
	}

	public String getQuartoDescricao() {
		return quartoDescricao;
	}

	public void setQuartoDescricao(String quartoDescricao) {
		this.quartoDescricao = quartoDescricao;
	}

	public FatContasHospitalares getContaHospitalarOld() {
		return contaHospitalarOld;
	}

	public void setContaHospitalarOld(FatContasHospitalares contaHospitalarOld) {
		this.contaHospitalarOld = contaHospitalarOld;
	}

	public String getDataOldStr() {
		return dataOldStr;
	}

	public void setDataOldStr(String dataOldStr) {
		this.dataOldStr = dataOldStr;
	}

	public Date getDataOld() {
		return dataOld;
	}

	public void setDataOld(Date dataOld) {
		this.dataOld = dataOld;
	}

	public String getDataAlteracaoStr() {
		return dataAlteracaoStr;
	}

	public void setDataAlteracaoStr(String dataAlteracaoStr) {
		this.dataAlteracaoStr = dataAlteracaoStr;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	
	
	
}
