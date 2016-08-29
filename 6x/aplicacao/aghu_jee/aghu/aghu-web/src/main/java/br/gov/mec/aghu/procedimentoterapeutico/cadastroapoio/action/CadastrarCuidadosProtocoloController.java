package br.gov.mec.aghu.procedimentoterapeutico.cadastroapoio.action;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.MptProtocoloCuidados;
import br.gov.mec.aghu.model.MptProtocoloCuidadosDia;
import br.gov.mec.aghu.model.MptVersaoProtocoloSessao;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ProtocoloMedicamentoSolucaoCuidadoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;

public class CadastrarCuidadosProtocoloController extends ActionController {

	private static final long serialVersionUID = 7377534750503842386L;	
	
	@EJB
	private IProcedimentoTerapeuticoFacade procedimentoTerapeuticoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private CadastrarProtocoloController cadastrarProtocoloController;
	
	private MpmCuidadoUsual cuidadoSelecionado;
	
	private MpmTipoFrequenciaAprazamento aprazamentoSelecionado;
	
	private Boolean complementoObrigatorio;
	
	private Boolean habilitarFrequencia;

	private Integer frequencia;
	
	private String complemento;
	
	private Date tempo;

	private MptProtocoloCuidados mptProtocoloCuidados;

	private MptVersaoProtocoloSessao mptVersaoProtocoloSessao;
	
	private Integer seqVersaoProtocoloSessaoCadastro;
	
	private Short ordem;	
	
	private Boolean edicao;
	
	private Boolean edicaoCheck;
	
	private Boolean habilitarSuggestion;
	
	private List<MptProtocoloCuidadosDia> diasCuidados;
	
	private MptProtocoloCuidadosDia diaEdicao;
	
	private Boolean readOnly;
	
	private static final String PAGE_CADASTRAR_PROTOCOLO = "procedimentoterapeutico-cadastraProtocolo";
	
	private Boolean validarFrequencia = true;
	
	public void iniciar(){		
		if(mptProtocoloCuidados != null && this.edicao){
			this.montarObjetos();			
		}else if(this.diaEdicao != null && this.edicaoCheck){
			this.montarObjetoDia();
		}else if(mptProtocoloCuidados != null && mptProtocoloCuidados.getSeq() != null && this.readOnly){
			habilitarSuggestion = true;
			this.montarObjetos();
		}else{
			habilitarSuggestion = false;
			mptProtocoloCuidados = new MptProtocoloCuidados();			
		}
		//this.verificarCamposRN03();
		this.verificarCamposON01();		
	}
	
	public List<MpmCuidadoUsual> listarCuidados(final String strPesquisa) {		
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.listarCuidados(strPesquisa),	this.procedimentoTerapeuticoFacade.listarCuidadosCount(strPesquisa));
	}
	
	public List<MpmTipoFrequenciaAprazamento> listarAprazamento(final String strPesquisa) {
		return this.returnSGWithCount(this.procedimentoTerapeuticoFacade.listarAprazamentos(strPesquisa),	this.procedimentoTerapeuticoFacade.listarAprazamentosCount(strPesquisa));
	}
	
	public String persistirProtocoloCuidados(){
		if(this.edicao){
			return this.verificarRN09();			
		}else if (this.edicaoCheck){		
			this.diaEdicao.setModificado(true);
			this.diaEdicao.setComplemento(this.complemento);			
			this.diaEdicao.setFrequencia(this.frequencia);
			this.diaEdicao.setTfqSeq(this.aprazamentoSelecionado.getSeq());
			if(this.tempo != null){
				Timestamp ts = new Timestamp(tempo.getTime());
				this.diaEdicao.setTempo(ts);		
			}
			this.procedimentoTerapeuticoFacade.atualizarMptProtocoloCuidadosDia(this.diaEdicao);
			apresentarMsgNegocio(Severity.INFO, "CUIDADO_ALTERADO_SUCESSO");
			this.limpar();
			return this.voltar();			
		}else{
			mptProtocoloCuidados.setVersaoProtocoloSessao(this.procedimentoTerapeuticoFacade.obterSituacaoProtocolo(this.seqVersaoProtocoloSessaoCadastro));
			mptProtocoloCuidados.setCriadoEm(new Date());
			List<ProtocoloMedicamentoSolucaoCuidadoVO> item = cadastrarProtocoloController.pesquisarListaTratamento();
			mptProtocoloCuidados.setOrdem((short) (item.size() + 1));
			try {
				mptProtocoloCuidados.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado()));
			} catch (ApplicationBusinessException e) {			 
				apresentarExcecaoNegocio(e);
			}
			mptProtocoloCuidados.setCuidadoUsual(cuidadoSelecionado);
			if(this.complemento != null){
				mptProtocoloCuidados.setComplemento(complemento);			
			}
			
			if(this.habilitarFrequencia){
				if(this.frequencia != null && this.frequencia != 0){
					mptProtocoloCuidados.setFrequencia(frequencia);
					this.validarFrequencia = true;
				}else{
					apresentarMsgNegocio(Severity.ERROR,  "MENSAGEM_FREQUENCIA_OBRIGATORIO");
					this.validarFrequencia = false;
				}
			}else{
				if(this.frequencia != null){
					mptProtocoloCuidados.setFrequencia(frequencia);			
				}				
			}
			
			mptProtocoloCuidados.setTipoFrequenciaAprazamento(aprazamentoSelecionado);
			if(this.tempo != null){			
				Timestamp ts = new Timestamp(tempo.getTime());
				mptProtocoloCuidados.setTempo(ts);			
			}
			if(this.validarFrequencia){
				procedimentoTerapeuticoFacade.persistirProtocoloCuidados(mptProtocoloCuidados);			
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CUIDADO_SALVO");
				this.limpar();				
			}
			return null;
		}
	}
	
	public void editarProtocoloCuidados(){
		mptProtocoloCuidados.setCuidadoUsual(cuidadoSelecionado);
		if(this.complemento != null){
			mptProtocoloCuidados.setComplemento(complemento);			
		}else{
			mptProtocoloCuidados.setComplemento(null);
		}
		
		if(this.habilitarFrequencia){
			if(this.frequencia != null && this.frequencia != 0){
				mptProtocoloCuidados.setFrequencia(frequencia);
				this.validarFrequencia = true;
			}else{
				this.validarFrequencia = false;
				apresentarMsgNegocio(Severity.ERROR,  "MENSAGEM_FREQUENCIA_OBRIGATORIO");
			}			
		}else{
			if(this.frequencia != null){
				mptProtocoloCuidados.setFrequencia(frequencia);			
			}else{
				mptProtocoloCuidados.setFrequencia(null);
			}			
		}
		
		mptProtocoloCuidados.setTipoFrequenciaAprazamento(aprazamentoSelecionado);
		if(this.tempo != null){			
			Timestamp ts = new Timestamp(tempo.getTime());
			mptProtocoloCuidados.setTempo(ts);			
		}else{			
			mptProtocoloCuidados.setTempo(null);
		}
		if(this.validarFrequencia){
			this.procedimentoTerapeuticoFacade.atualizarCuidadoProtocolo(this.mptProtocoloCuidados);
			this.edicao = false;			
		}
	}
	
	public String verificarRN09(){
		Boolean vericaritens = true;
		String tela = null;
		if(this.diasCuidados != null){
			for(MptProtocoloCuidadosDia item: diasCuidados){
				if(item.getModificado()){
					vericaritens = false;
					RequestContext.getCurrentInstance().execute("PF('modal_dias_modificados').show()");
					tela = null;
					break;
				}				
			}
			if(vericaritens){
				this.editarProtocoloCuidados();
				if(this.validarFrequencia){
					apresentarMsgNegocio(Severity.INFO, "CUIDADO_ALTERADO_SUCESSO");
				}
				tela = null;
			}
		}else{
			this.editarProtocoloCuidados();
			if(this.validarFrequencia){
				apresentarMsgNegocio(Severity.INFO, "CUIDADO_ALTERADO_SUCESSO");
			}
			tela = null;
		}
		return tela;
	}
	
	public String alterarTodosDiasModificados(){
		this.editarProtocoloCuidados();
		for(MptProtocoloCuidadosDia item: diasCuidados){
			if(item.getModificado()){
				item.setCduSeq(this.mptProtocoloCuidados.getCuidadoUsual().getSeq());
				item.setComplemento(this.mptProtocoloCuidados.getComplemento());
				item.setFrequencia(this.mptProtocoloCuidados.getFrequencia());
				item.setProtocoloCuidados(this.mptProtocoloCuidados);
				item.setTempo(this.mptProtocoloCuidados.getTempo());
				item.setTfqSeq(this.mptProtocoloCuidados.getTipoFrequenciaAprazamento().getSeq());			
				item.setModificado(false);
				this.procedimentoTerapeuticoFacade.alterarDias(item);
			}
		}
		return this.voltar();
	}
	
	public String alterarDiasModificados(){
		this.editarProtocoloCuidados();
		for(MptProtocoloCuidadosDia item: diasCuidados){
			if(!item.getModificado()){
				item.setCduSeq(this.mptProtocoloCuidados.getCuidadoUsual().getSeq());
				item.setComplemento(this.mptProtocoloCuidados.getComplemento());
				item.setFrequencia(this.mptProtocoloCuidados.getFrequencia());
				item.setProtocoloCuidados(this.mptProtocoloCuidados);
				item.setTempo(this.mptProtocoloCuidados.getTempo());
				item.setTfqSeq(this.mptProtocoloCuidados.getTipoFrequenciaAprazamento().getSeq());				
				this.procedimentoTerapeuticoFacade.alterarDias(item);				
			}
		}
		return this.voltar();
	}
	
	private void montarObjetos(){
		this.cuidadoSelecionado = this.procedimentoTerapeuticoFacade.obterCuidadoPorSeq(this.mptProtocoloCuidados.getCuidadoUsual().getSeq());
		this.complemento = this.mptProtocoloCuidados.getComplemento();
		this.frequencia = this.mptProtocoloCuidados.getFrequencia();
		this.aprazamentoSelecionado = this.mptProtocoloCuidados.getTipoFrequenciaAprazamento();
		this.tempo = this.mptProtocoloCuidados.getTempo();
	}
	
	public void limpar(){
		this.cuidadoSelecionado = null;
		this.complemento = null;
		this.setFrequencia(null);
		this.aprazamentoSelecionado = null;
		this.tempo = null;
		this.iniciar();
	}
	
	private void montarObjetoDia(){
		this.cuidadoSelecionado = this.procedimentoTerapeuticoFacade.obterCuidadoPorSeq(this.diaEdicao.getCduSeq());
		this.complemento = this.diaEdicao.getComplemento();
		this.frequencia = this.diaEdicao.getFrequencia();
		this.aprazamentoSelecionado = this.procedimentoTerapeuticoFacade.obterAprazamento(this.diaEdicao.getTfqSeq());
		this.tempo = this.diaEdicao.getTempo();		
	}
	
	public Integer getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Integer frequencia) {
		this.frequencia = frequencia;
	}

	public String voltar(){
		if(this.edicao){
			this.edicao = false;
		}else if(this.edicaoCheck){
			this.edicaoCheck = false;
		}
		this.limpar();
		return PAGE_CADASTRAR_PROTOCOLO;
	}
	
	public void verificarCamposRN03(){
		if(null != this.cuidadoSelecionado){
			if(null != this.cuidadoSelecionado.getMpmTipoFreqAprazamentos()){
				this.aprazamentoSelecionado = this.cuidadoSelecionado.getMpmTipoFreqAprazamentos();	
				this.verificarCamposON01();
			}
			if(null != this.cuidadoSelecionado.getFrequencia()){
				this.frequencia = this.cuidadoSelecionado.getFrequencia().intValue();
			}
			if(null != this.cuidadoSelecionado.getIndDigitaComplemento()){
				complementoObrigatorio = this.cuidadoSelecionado.getIndDigitaComplemento();			
			}else{
				complementoObrigatorio = false;
			}
		}else{
			complementoObrigatorio = false;
		}
	}
	
	public void verificarCamposON01(){
		if(this.aprazamentoSelecionado != null){
			if(this.aprazamentoSelecionado.getIndDigitaFrequencia()){
				habilitarFrequencia = true; 
			}else{
				habilitarFrequencia = false;
			}			
		}else{
			habilitarFrequencia = false;
		}		
		if(this.habilitarFrequencia.equals(false) && this.aprazamentoSelecionado != null){
			this.frequencia = null;
		}
		
		if(this.habilitarFrequencia.equals(false) && this.aprazamentoSelecionado == null){
			this.frequencia = null;
		}		
	}
	
	public Boolean verificarStatusCancelar(){
		if(this.edicao || this.edicaoCheck){
			return true;
		}else{
			return false;
		}
	}
	
	public Boolean verificarStatusVoltar(){
		if(this.edicao || this.edicaoCheck){
			return false;
		}else{
			return true;
		}
	}
	
	public void  limparCamposRemocaoSuggestion(){
		this.aprazamentoSelecionado = null;
		this.frequencia = null;
	}
		
//	GETTERS E SETTERS
	public MpmCuidadoUsual getCuidadoSelecionado() {
		return cuidadoSelecionado;
	}

	public void setCuidadoSelecionado(MpmCuidadoUsual cuidadoSelecionado) {
		this.cuidadoSelecionado = cuidadoSelecionado;
	}

	public MpmTipoFrequenciaAprazamento getAprazamentoSelecionado() {
		return aprazamentoSelecionado;
	}

	public void setAprazamentoSelecionado(MpmTipoFrequenciaAprazamento aprazamentoSelecionado) {
		this.aprazamentoSelecionado = aprazamentoSelecionado;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}
	
	public Boolean getHabilitarFrequencia() {
		return habilitarFrequencia;
	}

	public void setHabilitarFrequencia(Boolean habilitarFrequencia) {
		this.habilitarFrequencia = habilitarFrequencia;
	}

	public MptProtocoloCuidados getMptProtocoloCuidados() {
		return mptProtocoloCuidados;
	}

	public void setMptProtocoloCuidados(MptProtocoloCuidados mptProtocoloCuidados) {
		this.mptProtocoloCuidados = mptProtocoloCuidados;
	}
	
	public MptVersaoProtocoloSessao getMptVersaoProtocoloSessao() {
		return mptVersaoProtocoloSessao;
	}

	public void setMptVersaoProtocoloSessao(MptVersaoProtocoloSessao mptVersaoProtocoloSessao) {
		this.mptVersaoProtocoloSessao = mptVersaoProtocoloSessao;
	}

	public Date getTempo() {
		return tempo;
	}

	public void setTempo(Date tempo) {
		this.tempo = tempo;
	}

	public Boolean getComplementoObrigatorio() {
		return complementoObrigatorio;
	}

	public void setComplementoObrigatorio(Boolean complementoObrigatorio) {
		this.complementoObrigatorio = complementoObrigatorio;
	}

	public Integer getSeqVersaoProtocoloSessaoCadastro() {
		return seqVersaoProtocoloSessaoCadastro;
	}

	public void setSeqVersaoProtocoloSessaoCadastro(
			Integer seqVersaoProtocoloSessaoCadastro) {
		this.seqVersaoProtocoloSessaoCadastro = seqVersaoProtocoloSessaoCadastro;
	}

	public Short getOrdem() {
		return ordem;
	}

	public void setOrdem(Short ordem) {
		this.ordem = ordem;
	}

	public Boolean getEdicao() {
		return edicao;
	}

	public void setEdicao(Boolean edicao) {
		this.edicao = edicao;
	}

	public Boolean getHabilitarSuggestion() {
		return habilitarSuggestion;
	}

	public void setHabilitarSuggestion(Boolean habilitarSuggestion) {
		this.habilitarSuggestion = habilitarSuggestion;
	}

	public List<MptProtocoloCuidadosDia> getDiasCuidados() {
		return diasCuidados;
	}

	public void setDiasCuidados(List<MptProtocoloCuidadosDia> diasCuidados) {
		this.diasCuidados = diasCuidados;
	}

	public Boolean getEdicaoCheck() {
		return edicaoCheck;
	}

	public void setEdicaoCheck(Boolean edicaoCheck) {
		this.edicaoCheck = edicaoCheck;
	}

	public MptProtocoloCuidadosDia getDiaEdicao() {
		return diaEdicao;
	}

	public void setDiaEdicao(MptProtocoloCuidadosDia diaEdicao) {
		this.diaEdicao = diaEdicao;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getValidarFrequencia() {
		return validarFrequencia;
	}

	public void setValidarFrequencia(Boolean validarFrequencia) {
		this.validarFrequencia = validarFrequencia;
	}
}
