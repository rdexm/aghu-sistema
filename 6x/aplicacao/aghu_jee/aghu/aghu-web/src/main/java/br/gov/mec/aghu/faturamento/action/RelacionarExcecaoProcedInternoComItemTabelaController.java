package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.VFatConvPlanoGrupoProcedVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatExcCnvGrpItemProc;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("PMD.AghuTooManyMethods")
public class RelacionarExcecaoProcedInternoComItemTabelaController extends ActionController {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 8227640431751166586L;

	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	//Carregados por parametro
	private Short cpgCphCspCnvCodigo; 
	private Byte cpgCphCspSeq;
	private Short cpgCphPhoSeq;
	private Short cpgGrcSeq;
	
	private String cpgGrcDescricao;
	
	
	//usados nas suggestions
	private FatProcedHospInternos procedHospInterno; 
	private VFatConvPlanoGrupoProcedVO tabela;
	private VFatConvPlanoGrupoProcedVO convenio;
	private VFatConvPlanoGrupoProcedVO plano;
	private VFatConvPlanoGrupoProcedVO tabelaItem;
	//usado na suggestion ## PROCEDIMENTO REALIZADO ##
	private FatItensProcedHospitalar itemProcedHospRealizado;
	//usado na suggestion ## ITEM PROCEDIMENTO ##
	private FatItensProcedHospitalar itemProcedHosp;
	private FatProcedimentosHospitalares procedHospitalar;
	
	//usado no selectOneMenu
	private FatTipoTransplante tipoTransplante;
	
	//usado no checkbox
	private Boolean exigeNotaFiscal;
	
	private List<FatExcCnvGrpItemProc> listaExcCnvGrpItemProc;
	
	//usado na remocao de registros da listagem
	private Integer seqExc;
	
	//Usado para exibir a listagem.
	private boolean exibirPanelInferior = false;
	
	//Usado na manipulacao de registros da lista
	private int item;

	private boolean exibirModalNotaFiscal = false;
	
	
	public enum RelacionarExcecaoProcedInternoComItemTabelaControllerExceptionCode implements BusinessExceptionCode {
		EXCECAO_JA_ASSOCIADA, EXCECOES_GRAVADAS_COM_SUCESSO, EXCECAO_EXCLUIDA_COM_SUCESSO
		;
	}
	
	@SuppressWarnings("PMD.NPathComplexity")
	@PostConstruct
	public void inicio(){
		begin(conversation);
		try{	
			AghParametros pTabela = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TABELA_FATUR_PADRAO);
			this.cpgCphPhoSeq = pTabela.getVlrNumerico().shortValue();
			List<VFatConvPlanoGrupoProcedVO> listaTabela = this.faturamentoFacade.listarTabelas(this.cpgCphPhoSeq.toString()); 
			if(listaTabela != null && !listaTabela.isEmpty()){
				this.tabela = listaTabela.get(0);
				
				this.cpgGrcSeq = this.tabela.getGrcSeq(); 
				this.cpgGrcDescricao = this.tabela.getGrcDescricao();
				
			}
			
			AghParametros convenio = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS);
			this.cpgCphCspCnvCodigo = convenio.getVlrNumerico().shortValue();
			List<VFatConvPlanoGrupoProcedVO> listaConvenio = this.faturamentoFacade.listarConvenios(this.cpgCphCspCnvCodigo.toString(), this.tabela != null ? this.tabela.getGrcSeq() : null, this.cpgCphPhoSeq); 
			if(listaConvenio != null && !listaConvenio.isEmpty()){
				this.convenio = listaConvenio.get(0);
			}
			
			AghParametros plano = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SUS_PLANO_INTERNACAO);
			this.cpgCphCspSeq = plano.getVlrNumerico().byteValue();
			List<VFatConvPlanoGrupoProcedVO> listaPlano = this.faturamentoFacade.listarPlanos(this.cpgCphCspSeq.toString(), this.tabela != null ? this.tabela.getGrcSeq() : null, 
					this.tabela != null ? this.tabela.getCphPhoSeq(): null, this.convenio != null ? this.convenio.getCphCspCnvCodigo() : null); 
			if(listaPlano != null && !listaPlano.isEmpty()){
				this.plano = listaPlano.get(0);
			}
	
		}catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	
	public void pesquisar(){
		//Limpa os campos de adicionar sempre antes de pesquisar
		this.tipoTransplante = null;
		this.itemProcedHospRealizado = null;
		this.exigeNotaFiscal = null;
		this.tabelaItem = null;
		this.itemProcedHosp = null;
		
		if(this.procedHospInterno != null) {
		
			this.listaExcCnvGrpItemProc = this.faturamentoFacade.listarFatExcCnvGrpItemProcPorPlanoConvenioProcedInternoETabela(this.procedHospInterno.getSeq(), this.cpgCphPhoSeq, this.cpgCphCspCnvCodigo, this.cpgCphCspSeq);
			
			if(this.listaExcCnvGrpItemProc != null && !this.listaExcCnvGrpItemProc.isEmpty()){
				this.determinarEstadoNotaFiscal(); 
			}
			
			//carregar o checkbox de nota fiscal conforme o valor do primeiro elemento da lista
			if(this.listaExcCnvGrpItemProc != null && !this.listaExcCnvGrpItemProc.isEmpty()){
				this.exigeNotaFiscal = this.listaExcCnvGrpItemProc.get(0).getIndExigeNotaFiscal();
			}
			
			this.exibirPanelInferior = true;
		}
	}
	
	public void alterarValorNotaFiscal(){
		if(this.exigeNotaFiscal){
			this.exigeNotaFiscal = false;
		}else{
			this.exigeNotaFiscal = true;
		}
	}

	public void editar() {
		FatExcCnvGrpItemProc exc = this.listaExcCnvGrpItemProc.get(this.item);
		if(exc.getIndExigeNotaFiscal() == null || exc.getIndExigeNotaFiscal().equals(false)){
			exc.setIndExigeNotaFiscal(true);
		}else{
			exc.setIndExigeNotaFiscal(false);
		}
		
		this.faturamentoFacade.persistirExcCnvGrpItemProc(exc);

		apresentarMsgNegocio(RelacionarExcecaoProcedInternoComItemTabelaControllerExceptionCode.EXCECOES_GRAVADAS_COM_SUCESSO.toString());

		pesquisar();
	}

	public void remover(final FatExcCnvGrpItemProc item) {
        this.faturamentoFacade.removerExcCnvGrpItemProc(item);
		
	    apresentarMsgNegocio(RelacionarExcecaoProcedInternoComItemTabelaControllerExceptionCode.EXCECAO_EXCLUIDA_COM_SUCESSO.toString());
		
	    pesquisar();
	}

	
	
	public void adicionar() throws ApplicationBusinessException{
		boolean existe = false;
		try{
			if(this.listaExcCnvGrpItemProc != null && !this.listaExcCnvGrpItemProc.isEmpty()){
				for (FatExcCnvGrpItemProc elemento : this.listaExcCnvGrpItemProc) {
					if(elemento.getIphSeqRealizado().equals(this.itemProcedHospRealizado.getId().getSeq()) 
							&& elemento.getIphSeq().equals(this.itemProcedHosp.getId().getSeq())
							&& elemento.getIphPhoSeqRealizado().equals(this.itemProcedHospRealizado.getId().getPhoSeq())
							&& elemento.getIphPhoSeq().equals(this.itemProcedHosp.getId().getPhoSeq())
							&& ((elemento.getFatTipoTransplante() == null && this.tipoTransplante == null)
								|| (elemento.getFatTipoTransplante() != null && this.tipoTransplante != null 
										&& elemento.getFatTipoTransplante().equals(this.tipoTransplante)
									)
								) 
						){
						existe = true;
						break;
					}
				}
			}
			
			if(!existe){
				
				FatExcCnvGrpItemProc exc = new FatExcCnvGrpItemProc();
				
				exc.setCpgCphCspCnvCodigo(this.cpgCphCspCnvCodigo);
				exc.setCpgCphCspSeq(this.cpgCphCspSeq);
				exc.setCpgCphPhoSeq(this.cpgCphPhoSeq);
				exc.setCpgGrcSeq(this.cpgGrcSeq);
				exc.setFatTipoTransplante(this.tipoTransplante);
				exc.setTtrCodigo(this.tipoTransplante != null ? this.tipoTransplante.getCodigo() : null);//TODO remover quando o mapeamento for corrigido.
				exc.setIndExigeNotaFiscal(this.exigeNotaFiscal);
				exc.setProcedimentoHospitalarInterno(this.procedHospInterno);
				exc.setItemProcedimentoHospitalar(this.itemProcedHosp);
				exc.setItemProcedimentoHospitalarRealizado(this.itemProcedHospRealizado);
				exc.setPhiSeq(this.procedHospInterno.getSeq());								//TODO remover quando o mapeamento for corrigido.
				exc.setIphPhoSeq(this.itemProcedHosp.getId().getPhoSeq());					//TODO remover quando o mapeamento for corrigido.
				exc.setIphSeq(this.itemProcedHosp.getId().getSeq());						//TODO remover quando o mapeamento for corrigido.
				exc.setIphPhoSeqRealizado(this.itemProcedHospRealizado.getId().getPhoSeq());//TODO remover quando o mapeamento for corrigido.
				exc.setIphSeqRealizado(this.itemProcedHospRealizado.getId().getSeq());		//TODO remover quando o mapeamento for corrigido.
				
				this.listaExcCnvGrpItemProc.add(exc);//Adiciona na lista, somente para exibicao.
				
				//Limpa os campos apos adicionar na lista
				this.tipoTransplante = null;
				this.itemProcedHospRealizado = null;
//				this.exigeNotaFiscal = null;
				this.tabelaItem = null;
				this.itemProcedHosp = null;
				
				this.faturamentoFacade.persistirExcCnvGrpItemProc(exc);
				
				apresentarMsgNegocio(RelacionarExcecaoProcedInternoComItemTabelaControllerExceptionCode.EXCECOES_GRAVADAS_COM_SUCESSO.toString());
			
				pesquisar();
				
			}else{
				throw new ApplicationBusinessException(RelacionarExcecaoProcedInternoComItemTabelaControllerExceptionCode.EXCECAO_JA_ASSOCIADA);
			}
		}catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void determinarEstadoNotaFiscal(){
		if(this.listaExcCnvGrpItemProc != null && !this.listaExcCnvGrpItemProc.isEmpty() && this.listaExcCnvGrpItemProc.size() > 1){
			for (int i = 1; i < this.listaExcCnvGrpItemProc.size(); i++) {
				if(this.listaExcCnvGrpItemProc.get(0).getIndExigeNotaFiscal() != this.listaExcCnvGrpItemProc.get(i).getIndExigeNotaFiscal()){
					this.exibirModalNotaFiscal = false;
					break;
				}else if(this.exigeNotaFiscal != this.listaExcCnvGrpItemProc.get(0).getIndExigeNotaFiscal()){
					this.exibirModalNotaFiscal = true;
				}
			}
		}else if(this.listaExcCnvGrpItemProc != null && !this.listaExcCnvGrpItemProc.isEmpty()){
			this.exibirModalNotaFiscal = true;
		}
	}
	
	public void limparPesquisa() {
		this.procedHospInterno = null;
		this.tabela = null;
		this.cpgGrcSeq = null;
		this.cpgGrcDescricao = null;
		this.convenio = null;
		this.plano = null;
		
		this.exibirPanelInferior = false;
		this.exibirModalNotaFiscal = false;
		
		inicio();
	}
	
	public void limparVoltar() {
		this.procedHospInterno = null;
		
		this.exibirPanelInferior = false;
		this.exibirModalNotaFiscal = false;
		
		//this.refreshListagem();
	}
	
//	private void refreshListagem(){
//		//Faz um refresh na lista que esta em cache para nao exibir elementos nao sincronizados com o banco.
//		if(this.listaExcCnvGrpItemProc != null && !this.listaExcCnvGrpItemProc.isEmpty()){
//			for (FatExcCnvGrpItemProc element : this.listaExcCnvGrpItemProc) {
//				this.faturamentoFacade.refresh(element);
//			}
//		}
//	}
	
	//#############################################//
	//### Metodos abaixo usados nas suggestions ###//
	//#############################################//
	
	//Suggestion: Procedimento Hospitalar Interno
	public List<FatProcedHospInternos> listarPhis(String objPesquisa){
		return this.returnSGWithCount(this.faturamentoFacade.listarPhisAtivosPorSeqEDescricao(objPesquisa),listarPhisCount(objPesquisa));
	}
	
	public Long listarPhisCount(String objPesquisa){
		return this.faturamentoFacade.listarPhisAtivosPorSeqEDescricaoCount(objPesquisa);
	}

	//Suggestions: Tabelas e Tabela Itens
	public List<VFatConvPlanoGrupoProcedVO> listarTabelas(String objPesquisa){
		return this.returnSGWithCount(this.faturamentoFacade.listarTabelas(objPesquisa),listarTabelasCount(objPesquisa));
	}
	
	public Long listarTabelasCount(Object objPesquisa){
		return this.faturamentoFacade.listarTabelasCount(objPesquisa);
	}

	
	//Auxiliar para a suggestion: Tabelas
	public void executarAposLimparSuggestionTabela(){
		this.cpgCphPhoSeq = null;
		this.cpgGrcSeq = null;
		this.cpgGrcDescricao = null;
		this.cpgCphCspCnvCodigo = null; 
		this.cpgCphCspSeq = null;

		this.convenio = null;
		this.plano = null;
	}

	public void executarAposSelecionarSuggestionTabela(){
		this.cpgCphPhoSeq = this.tabela.getCphPhoSeq();
		this.cpgGrcSeq = this.tabela.getGrcSeq(); 
		this.cpgGrcDescricao = this.tabela.getGrcDescricao();
	}
	
	//Auxiliar para a suggestion: Tabela Itens
	public void executarAposLimparSuggestionTabelaItens(){
		 this.itemProcedHosp = null;
	}
	
	
	//Suggestion: Convenios
	public List<VFatConvPlanoGrupoProcedVO> listarConvenios(Object objPesquisa){
		return this.faturamentoFacade.listarConvenios(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq());
	}

	public Long listarConveniosCount(Object objPesquisa){
		return this.faturamentoFacade.listarConveniosCount(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq());
	}

	public void executarAposSelecionarSuggestionConvenio(){
		this.cpgCphCspCnvCodigo = this.convenio.getCphCspCnvCodigo();
	}
	
	public void executarAposLimparSuggestionConvenio(){
		this.cpgCphCspCnvCodigo = null; 

		this.cpgCphCspSeq = null;
		this.plano = null;
	}
	
	//Suggestion: Planos
	public List<VFatConvPlanoGrupoProcedVO> listarPlanos(Object objPesquisa){
		return this.faturamentoFacade.listarPlanos(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq(), this.convenio.getCphCspCnvCodigo());
	}
	
	public Long listarPlanosCount(Object objPesquisa){
		return this.faturamentoFacade.listarPlanosCount(objPesquisa, this.cpgGrcSeq, this.tabela.getCphPhoSeq(), this.convenio.getCphCspCnvCodigo());
	}

	public void executarAposSelecionarSuggestionPlano(){
		this.cpgCphCspSeq = this.plano.getCphCspSeq();
	}
	
	public void executarAposLimparSuggestionPlano(){
		this.cpgCphCspSeq = null;
	}
	
	
	//Suggestion: Itens Procecimento Hospitalar
	public List<FatItensProcedHospitalar> listarFatItensProcedHospitalar(String objPesquisa) {
		return this.returnSGWithCount(this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeq(objPesquisa, this.tabela.getCphPhoSeq()),listarFatItensProcedHospitalarCount(objPesquisa));
	}
	
	public Long listarFatItensProcedHospitalarCount(String objPesquisa) {
		return this.faturamentoFacade.listarItensProcedHospPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, this.tabela.getCphPhoSeq());	
	}
	
	//Suggestion: Itens Procecimento Hospitalar Ativos
	public List<FatItensProcedHospitalar> listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(String objPesquisa){
		return this.returnSGWithCount(this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeq(objPesquisa, this.tabelaItem.getCphPhoSeq(), FatItensProcedHospitalar.Fields.PHO_SEQ.toString()),listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa));
	}
	
	public Long listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(String objPesquisa){
		return this.faturamentoFacade.listarItensProcedHospAtivosPorCodTabelaOuDescricaoEPhoSeqCount(objPesquisa, this.tabelaItem.getCphPhoSeq());
	}

	//SelectOneMenu: Transplante
	public List<FatTipoTransplante> listarTodosOsTiposTransplante(){
		return this.faturamentoFacade.listarTodosOsTiposTransplante();
	}

	
	//############################	
	//### GETTERs and SETTERs ####
	//############################
		

	public Short getCpgCphCspCnvCodigo() {
		return cpgCphCspCnvCodigo;
	}

	public void setCpgCphCspCnvCodigo(Short cpgCphCspCnvCodigo) {
		this.cpgCphCspCnvCodigo = cpgCphCspCnvCodigo;
	}

	public Byte getCpgCphCspSeq() {
		return cpgCphCspSeq;
	}

	public void setCpgCphCspSeq(Byte cpgCphCspSeq) {
		this.cpgCphCspSeq = cpgCphCspSeq;
	}

	public Short getCpgCphPhoSeq() {
		return cpgCphPhoSeq;
	}

	public void setCpgCphPhoSeq(Short cpgCphPhoSeq) {
		this.cpgCphPhoSeq = cpgCphPhoSeq;
	}

	public Short getCpgGrcSeq() {
		return cpgGrcSeq;
	}

	public void setCpgGrcSeq(Short cpgGrcSeq) {
		this.cpgGrcSeq = cpgGrcSeq;
	}

	public String getCpgGrcDescricao() {
		return cpgGrcDescricao;
	}

	public void setCpgGrcDescricao(String cpgGrcDescricao) {
		this.cpgGrcDescricao = cpgGrcDescricao;
	}

	public FatProcedHospInternos getProcedHospInterno() {
		return procedHospInterno;
	}

	public void setProcedHospInterno(FatProcedHospInternos procedHospInterno) {
		this.procedHospInterno = procedHospInterno;
	}

	public VFatConvPlanoGrupoProcedVO getTabela() {
		return tabela;
	}

	public void setTabela(VFatConvPlanoGrupoProcedVO tabela) {
		this.tabela = tabela;
	}

	public VFatConvPlanoGrupoProcedVO getConvenio() {
		return convenio;
	}

	public void setConvenio(VFatConvPlanoGrupoProcedVO convenio) {
		this.convenio = convenio;
	}

	public VFatConvPlanoGrupoProcedVO getPlano() {
		return plano;
	}

	public void setPlano(VFatConvPlanoGrupoProcedVO plano) {
		this.plano = plano;
	}

	public VFatConvPlanoGrupoProcedVO getTabelaItem() {
		return tabelaItem;
	}

	public void setTabelaItem(VFatConvPlanoGrupoProcedVO tabelaItem) {
		this.tabelaItem = tabelaItem;
	}

	public FatItensProcedHospitalar getItemProcedHospRealizado() {
		return itemProcedHospRealizado;
	}

	public void setItemProcedHospRealizado(
			FatItensProcedHospitalar itemProcedHospRealizado) {
		this.itemProcedHospRealizado = itemProcedHospRealizado;
	}

	public FatItensProcedHospitalar getItemProcedHosp() {
		return itemProcedHosp;
	}

	public void setItemProcedHosp(FatItensProcedHospitalar itemProcedHosp) {
		this.itemProcedHosp = itemProcedHosp;
	}

	public FatTipoTransplante getTipoTransplante() {
		return tipoTransplante;
	}

	public void setTipoTransplante(FatTipoTransplante tipoTransplante) {
		this.tipoTransplante = tipoTransplante;
	}

	public Boolean getExigeNotaFiscal() {
		return exigeNotaFiscal;
	}

	public void setExigeNotaFiscal(Boolean exigeNotaFiscal) {
		this.exigeNotaFiscal = exigeNotaFiscal;
	}

	public List<FatExcCnvGrpItemProc> getListaExcCnvGrpItemProc() {
		return listaExcCnvGrpItemProc;
	}

	public void setListaExcCnvGrpItemProc(
			List<FatExcCnvGrpItemProc> listaExcCnvGrpItemProc) {
		this.listaExcCnvGrpItemProc = listaExcCnvGrpItemProc;
	}

	public FatProcedimentosHospitalares getProcedHospitalar() {
		return procedHospitalar;
	}

	public void setProcedHospitalar(FatProcedimentosHospitalares procedHospitalar) {
		this.procedHospitalar = procedHospitalar;
	}

	public Integer getSeqExc() {
		return seqExc;
	}

	public void setSeqExc(Integer seqExc) {
		this.seqExc = seqExc;
	}

	public boolean isExibirPanelInferior() {
		return exibirPanelInferior;
	}

	public void setExibirPanelInferior(boolean exibirPanelInferior) {
		this.exibirPanelInferior = exibirPanelInferior;
	}

	public int getItem() {
		return item;
	}

	public void setItem(int item) {
		this.item = item;
	}

	public boolean isExibirModalNotaFiscal() {
		return exibirModalNotaFiscal;
	}


	public void setExibirModalNotaFiscal(boolean exibirModalNotaFiscal) {
		this.exibirModalNotaFiscal = exibirModalNotaFiscal;
	}


}