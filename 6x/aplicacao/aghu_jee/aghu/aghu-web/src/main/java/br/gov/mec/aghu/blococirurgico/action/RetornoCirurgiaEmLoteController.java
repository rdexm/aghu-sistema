package br.gov.mec.aghu.blococirurgico.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaProcedimentoVO;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaTelaVO;
import br.gov.mec.aghu.blococirurgico.vo.MbcProcEspPorCirurgiasVO;
import br.gov.mec.aghu.blococirurgico.vo.RetornoCirurgiaEmLotePesquisaVO;
import br.gov.mec.aghu.blococirurgico.vo.RetornoCirurgiaEmLoteVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatConvenioSaudePlanoId;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class RetornoCirurgiaEmLoteController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 inicio();
	}

	private static final Log LOG = LogFactory.getLog(RetornoCirurgiaEmLoteController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 325785298776089820L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private RegistroCirurgiaRealizadaController registroCirurgiaRealizadaController;
	
	private RetornoCirurgiaEmLoteVO tela;
	private RetornoCirurgiaEmLotePesquisaVO cirurgiaSelecionada;
	
	private Integer pacCodigo;
	private Integer crgSeq;	
	private String voltarPara;

	
	private final String PAGE_REGISTRO_CIRURGIA_REALIZADA = "blococirurgico-registroCirurgiaRealizada";
	private final String PAGE_REGISTRO_CIRURGIA_LOTE = "blococirurgico-retornoCirurgiaEmLote";
	
	public void inicio(){
		if(this.tela == null){
			this.tela = new RetornoCirurgiaEmLoteVO();
			this.tela.setCirurgiaParaEditar(new RetornoCirurgiaEmLotePesquisaVO());
		}
		//Refaz pesquisa quando vem do registro para uma nota cofirmada não desaparecer da tela de pesquisa
		if(getVoltarPara()!= null && getVoltarPara().equals("registroCirurgiaRealizada")){
			this.setVoltarPara("");
			this.pesquisar();
		}

		this.tela.setCirurgiaParaEditar(null);
	}
	
	public List<AghUnidadesFuncionais>  buscarUnidadesFuncionais(String objPesquisa){
		return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(objPesquisa);
	}
	
	public List<MbcSalaCirurgica> pesquisarSalaCirurgia(String objPesquisa){
		return this.blocoCirurgicoFacade.pesquisarSalaCirurgia(objPesquisa, tela.getAghUnidadesFuncionaisSuggestionBox() != null ? tela.getAghUnidadesFuncionaisSuggestionBox().getSeq() : null);
	}
	 
	public void pesquisar(){
		
		Short unidadeFuncionalSeq = tela.getAghUnidadesFuncionaisSuggestionBox() != null ? tela.getAghUnidadesFuncionaisSuggestionBox().getSeq() : null;
		Short sala = tela.getMbcSalaCirurgicaSuggestionBox() != null ? tela.getMbcSalaCirurgicaSuggestionBox().getId().getSeqp() : null;
		
		this.tela.setListaMbcCirurgiaPesquisada(
				this.blocoCirurgicoFacade.pesquisarRetornoCirurgiasEmLote(
						unidadeFuncionalSeq, 
						this.tela.getDataCirurgia(), 
						sala, 
						tela.getProntuario()));
		
		for(MbcCirurgias cirurgia : this.tela.getListaMbcCirurgiaPesquisada()){
			CirurgiaTelaVO vo = new CirurgiaTelaVO();
			vo.setCirurgia(cirurgia);
			String mensagem = this.blocoCirurgicoFacade.validarFaturamentoPacienteTransplantado(vo);
			if(mensagem != null){
				if ("MBC_00537".equalsIgnoreCase(mensagem)) {
					this.apresentarMsgNegocio(Severity.ERROR, mensagem, cirurgia.getPaciente().getNome());
				} else {
					this.apresentarMsgNegocio(Severity.ERROR, mensagem);
				}
			}
			
		}
		
		this.tela.fazerBindDaListaPesquisada();
		this.limparCamposEdicao();
	}	
	
	public String editarRegistroCirurgiaRealizada(RetornoCirurgiaEmLotePesquisaVO objectoSelecionado){
		//MbcCirurgias crg = blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(objectoSelecionado.getCirurgiaSeq());
		//this.setCrgSeq(crg.getSeq());
		registroCirurgiaRealizadaController.setCrgSeq(objectoSelecionado.getCirurgiaSeq());
		registroCirurgiaRealizadaController.setVoltarPara(PAGE_REGISTRO_CIRURGIA_LOTE);
		registroCirurgiaRealizadaController.inicio();		
		return PAGE_REGISTRO_CIRURGIA_REALIZADA;
	}

	
	
	public void carregarCirurgiaEdicao(RetornoCirurgiaEmLotePesquisaVO selecionado) throws BaseException{
			this.limparCamposEdicao();
			this.tela.setMostrarBotoesEdicao(Boolean.FALSE);
			buscarResponsavelAnestesiaProcedimento(selecionado);
	}

	public void carregarCirurgiaSelecionada() throws BaseException{
		this.limparCamposEdicao();
		this.tela.setMostrarBotoesEdicao(Boolean.FALSE);
		buscarResponsavelAnestesiaProcedimento(getCirurgiaSelecionada());

	}
	
	public void carregarCirurgiaSelecionadaEdicao(RetornoCirurgiaEmLotePesquisaVO selecionado) throws BaseException{
		this.limparCamposEdicao();
	    this.tela.setMostrarBotoesEdicao(Boolean.TRUE);
		this.tela.setCirurgiaParaEditar(selecionado);
		this.tela.setDominioAntesMudar(selecionado.getSituacao());
		buscarResponsavelAnestesiaProcedimento(selecionado);
		this.tela.getCirurgiaParaEditar().setPlano(this.faturamentoFacade.obterConvenioSaudePlanoPorChavePrimaria(selecionado.getPlano().getId()));
	}
	
	public List<FatConvenioSaudePlano> pesquisarConvenioSaudePlanos(final String objPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarConvenioSaudePlanos((String) objPesquisa),pesquisarCountConvenioSaudePlanos(objPesquisa));
	}
	
	public Long pesquisarCountConvenioSaudePlanos(final String objPesquisa) {
		final String strPesquisa = (String) objPesquisa;
		return this.blocoCirurgicoFacade.pesquisarCountConvenioSaudePlanos(strPesquisa);
	}
	
	/*
	 * MÉTODOS DA SELEÇÃO DE PLANO E CONVÊNIO
	 */
	public void selecionarPlanoConvenio() {
		if (this.tela.getPlanoId() != null && this.tela.getConvenioId() != null) {
			final FatConvenioSaudePlano plano = this.blocoCirurgicoFacade.obterPlanoPorId(this.tela.getPlanoId(), this.tela.getConvenioId());
			if (plano == null) {
				this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_CONVENIO_PLANO_NAO_ENCONTRADO", this.tela.getConvenioId(), this.tela.getPlanoId());
			}
			this.atribuirPlano(plano);
		}
	}
	
	public void atribuirPlano(final FatConvenioSaudePlano plano) {
		if (plano != null) {
			this.tela.getCirurgia().setConvenioSaudePlano(plano);
			this.tela.setConvenioId(plano.getConvenioSaude().getCodigo());
			this.tela.setPlanoId(plano.getId().getSeq());
			
		} else {
			this.tela.getCirurgia().setConvenioSaudePlano(new FatConvenioSaudePlano(new FatConvenioSaudePlanoId()));
			this.tela.setConvenioId(null);
			this.tela.setPlanoId(null);
		}
	}
	
	public void atribuirPlano() {
		if (this.tela.getPlano() != null) {
			this.tela.setConvenioId(this.tela.getPlano().getConvenioSaude().getCodigo());
			this.tela.setPlanoId(this.tela.getPlano().getId().getSeq());
		} else {
			this.tela.setPlano(null);
			this.tela.setConvenioId(null);
			this.tela.setPlanoId(null);
		}
	}
	
	public void limpar(){
		this.setTela(new RetornoCirurgiaEmLoteVO());
	}
	
	public void limparCamposEdicao(){
		
		this.tela.setCirurgiaParaEditar(null);
		this.tela.setListaAnestesia(null);
		this.tela.setListaResponsavel(null);
		this.tela.setProcedimentos(null);
		this.tela.setMostrarBotoesEdicao(Boolean.FALSE);
	}
	
	public void gravar(){
		
		try{
			Boolean existemRegistrosAlterados = Boolean.FALSE;
			String host = super.getEnderecoRedeHostRemoto();			
			for(RetornoCirurgiaEmLotePesquisaVO vo : this.tela.getListaBind()){
				if(Boolean.TRUE.equals(vo.getFoiAlterado()) || Boolean.TRUE.equals(vo.getDigitouNotaSala())){
					existemRegistrosAlterados = Boolean.TRUE;
					MbcCirurgias cirurgiaParaAlterar = this.blocoCirurgicoFacade.obterCirurgiaPorSeqServidor(vo.getCirurgiaSeq());
					cirurgiaParaAlterar.setDigitaNotaSala(vo.getDigitouNotaSala());
					cirurgiaParaAlterar.setDataInicioCirurgia(vo.getDataInicio());
					cirurgiaParaAlterar.setDataFimCirurgia(vo.getDataFim());
					cirurgiaParaAlterar.setSituacao(vo.getSituacao());
					FatConvenioSaude convenioSaude = this.faturamentoFacade.obterConvenioSaude(vo.getConvenio());
					cirurgiaParaAlterar.setConvenioSaude(convenioSaude);
					cirurgiaParaAlterar.setConvenioSaudePlano(vo.getPlano());
					
					this.blocoCirurgicoFacade.gravarListaRetornoCirurgiaEmLote(cirurgiaParaAlterar, host);
						
				}
			}
			
			this.tela.setCirurgiaParaEditar(null);
			if(existemRegistrosAlterados){				
				this.apresentarMsgNegocio(Severity.INFO, "RETORNO_CIRURGIA_EM_LOTE_SALVO");
				this.pesquisar();
			} else {
				this.apresentarMsgNegocio(Severity.INFO, "RETORNO_CIRURGIA_EM_LOTE_NENHUM_SALVO");
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
			//this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
			//this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
	}
	
	public void mudarSituacao(){
		try {
			this.blocoCirurgicoFacade.mudarSituacao(this.tela.getCirurgiaParaEditar());
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getMessage());
			this.tela.getCirurgiaParaEditar().setSituacao(this.tela.getDominioAntesMudar());
		}
	}

	private void buscarResponsavelAnestesiaProcedimento(
			RetornoCirurgiaEmLotePesquisaVO cirurgiaSelecionada) {
		MbcProfCirurgias profissionais = this.blocoCirurgicoFacade.buscarProfissionalResponsavel(cirurgiaSelecionada.getCirurgiaSeq());
		if (profissionais != null) {
			MbcProfAtuaUnidCirgs profAtua = this.blocoCirurgicoFacade.buscarNomeResponsavelCirurgia(profissionais);
			this.tela.adicionarNovoNomeResponsavelCirurgia(this.registroColaboradorFacade.obterNomePessoaServidor(profAtua.getId().getSerVinCodigo(), profAtua.getId().getSerMatricula()));
				
		}
		this.tela.setListaAnestesia(this.blocoCirurgicoFacade.obterTipoAnestesia(cirurgiaSelecionada.getCirurgiaSeq()));
		try {
			this.tela.setProcedimentos(this.blocoCirurgicoFacade.buscarListaMbcProcEspPorCirurgiasVO(this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(cirurgiaSelecionada.getCirurgiaSeq())));
			this.pupularProcedimentos(cirurgiaSelecionada);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}
	}
	
	private void pupularProcedimentos(RetornoCirurgiaEmLotePesquisaVO cirurgiaSelecionada) throws ApplicationBusinessException {
		MbcCirurgias cirurgias = this.blocoCirurgicoFacade.obterCirurgiaPorChavePrimaria(cirurgiaSelecionada.getCirurgiaSeq());
		for(MbcProcEspPorCirurgiasVO proc : this.tela.getProcedimentos()){
			CirurgiaTelaProcedimentoVO procedimentoVO = new CirurgiaTelaProcedimentoVO();
			procedimentoVO.setProcedimentoCirurgico(this.blocoCirurgicoFacade.obterProcedimentoCirurgico(proc.getProcedimentoCirurgicoSeq()));
			proc.setListaCodigoProcedimentos(this.blocoCirurgicoFacade.listarCodigoProcedimentos(procedimentoVO, cirurgias.getOrigemPacienteCirurgia()));
		}
	}

	public void alterar(){
		this.tela.getCirurgiaParaEditar().setFoiAlterado(Boolean.TRUE);
		this.limparCamposEdicao();
	}	

	public RetornoCirurgiaEmLoteVO getTela() {
		return tela;
	}

	public void setTela(RetornoCirurgiaEmLoteVO tela) {
		this.tela = tela;
	}

	public RetornoCirurgiaEmLotePesquisaVO getCirurgiaSelecionada() {
		return cirurgiaSelecionada;
	}

	public void setCirurgiaSelecionada(
			RetornoCirurgiaEmLotePesquisaVO cirurgiaSelecionada) {
		this.cirurgiaSelecionada = cirurgiaSelecionada;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
}
