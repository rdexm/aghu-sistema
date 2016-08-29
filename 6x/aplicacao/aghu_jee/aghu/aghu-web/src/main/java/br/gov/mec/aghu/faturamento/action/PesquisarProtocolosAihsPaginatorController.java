package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ProtocolosAihsVO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

public class PesquisarProtocolosAihsPaginatorController extends ActionController  {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6657828349500375975L;
	
	private static final Log LOG = LogFactory.getLog(PesquisarProtocolosAihsPaginatorController.class);
	
	private static final String PAGINA_ARQUIVOS = "gerarArquivoAutorizacaoSms";  // PAGINA DE ARQUIVOS
	
	private List<DominioSimNao> envios;
	private DominioSimNao envio;
	
	private Integer prontuario;
	private Integer codPaciente;
	private String nomePaciente;
	private Date dataInternacao;
	private Date dataAlta;
	private Date dataEnvio;
	private Integer conta;
	private String leito;
	
	private boolean checkBox;
	
	private boolean pesquisaAtiva;
	
	private List<ProtocolosAihsVO> list = new ArrayList<ProtocolosAihsVO>();
	
	private Boolean permissao;
	
	private String origem;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogado;
	
	@EJB
	private IPermissionService permissionService;

	@PostConstruct
	public void inicio() {
		begin(conversation, true);
		envios = new ArrayList<DominioSimNao>();
		envios.add(DominioSimNao.S);
		envios.add(DominioSimNao.N);
		limparPesquisa();
		permissao = this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "alterarProtocolosAIHs", "alterar");
	}

	public void init() {
		if(codPaciente != null){
			pesquisar();
		}
	}
	
	public void gravar(){
		persistirGrid();
	}
	
	public String arquivo(){
		if(validaGrid()){
			return redirectArquivos();
		}
		
		return null;
	}
	
	public String redirectArquivos(){
		return PAGINA_ARQUIVOS; 
	}
	
	public void pesquisar() {
		try {
			String envioString = null;
			if(envio != null){
				envioString = envio.toString();
			}
			list = faturamentoFacade.getProtocolosAihs(prontuario, nomePaciente, codPaciente, leito, conta,dataInternacao, dataAlta, dataEnvio, envioString);
			for (ProtocolosAihsVO item : list) {
				item.setPacienteTruncado(StringUtil.trunc(item.getPaciente(), true, (long)20));
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.setPesquisaAtiva(true);
	}

	public void limparPesquisa() {
		// linha 1
		prontuario = null;
		codPaciente = null;
		nomePaciente = null;
		// linha 2
		dataInternacao = null;
		dataAlta = null;
		dataEnvio = null;
		envio = null;
		// linha 3
		conta = null;
		leito = null;
		// listagem
		list = new ArrayList<ProtocolosAihsVO>();
		this.setPesquisaAtiva(false);
		setCheckBox(false);
	}
	
	public void toggleCheck(ProtocolosAihsVO item) {
	    Calendar calendar = Calendar.getInstance();  
        
        calendar.setTime(new Date()); 
        calendar.set(Calendar.HOUR_OF_DAY, 8); 
        calendar.set(Calendar.MINUTE, 0);  
        calendar.set(Calendar.SECOND, 0);  
		if(item.isEnvioBoolean() && item.getDataenviosms() == null){
			item.setDataenviosms(calendar.getTime());
		}
		else if(!item.isEnvioBoolean()){
			item.setDataenviosms(null);
		}
		item.setUpdate(true);
		setCheckBox(true);
    }
	
	public void toggleData(ProtocolosAihsVO item) {
		setCheckBox(true);
		item.setUpdate(true);
    }
	
	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}
	
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public Date getDataAlta() {
		return dataAlta;
	}

	public void setDataAlta(Date dataAlta) {
		this.dataAlta = dataAlta;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public List<DominioSimNao> getEnvios() {
		return envios;
	}

	public void setEnvios(List<DominioSimNao> envios) {
		this.envios = envios;
	}

	public DominioSimNao getEnvio() {
		return envio;
	}

	public void setEnvio(DominioSimNao envio) {
		this.envio = envio;
	}

	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public boolean isPesquisaAtiva() {
		return pesquisaAtiva;
	}

	public void setPesquisaAtiva(boolean pesquisaAtiva) {
		this.pesquisaAtiva = pesquisaAtiva;
	}

	public List<ProtocolosAihsVO> getList() {
		return list;
	}

	public void setList(List<ProtocolosAihsVO> list) {
		this.list = list;
	}

	public boolean isCheckBox() {
		return checkBox;
	}

	public void setCheckBox(boolean checkBox) {
		this.checkBox = checkBox;
	}
	
	
	public Boolean getPermissao() {
		return permissao;
	}

	public void setPermissao(Boolean permissao) {
		this.permissao = permissao;
	}

	public void persistirGrid(){
		if(validaDatas()){
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e1) {
				LOG.error("Exceção capturada:", e1);
			}
			RapServidores servidor = servidorLogado.obterServidorLogado();
			for (ProtocolosAihsVO item : list) {				
				try {
					faturamentoFacade.atualizarContaHospitalarProtocolosAih(item, false, nomeMicrocomputador, servidor.getDtFimVinculo());
					item.setUpdate(false);
				} catch (BaseException e) {
					apresentarExcecaoNegocio(e);
				}				
			}
			if(isCheckBox()){
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PROTOCOLO_AIH");
				setCheckBox(false);
			}
		}
	}
	
	public boolean validaGrid(){
		if(isCheckBox()){
			apresentarMsgNegocio(Severity.ERROR, "CONFIRMACAO_ALTERACAO_AIHS");
			return false;
		}
		return true;
	}
	
	public boolean validaDatas(){
		Date dataHoje = new Date();
		for (ProtocolosAihsVO item : list) {
			if(item.isUpdate() && item.getDataenviosms() != null){
				if(DateUtil.validaDataTruncadaMaior(dataHoje,item.getDataenviosms())){
					apresentarMsgNegocio(Severity.ERROR, "AVISO_DATA_ENVIO_AIHS");
					return false;
				}
			}
		}
		return true;
	}
	
	public String voltar() {
		return origem;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
}
