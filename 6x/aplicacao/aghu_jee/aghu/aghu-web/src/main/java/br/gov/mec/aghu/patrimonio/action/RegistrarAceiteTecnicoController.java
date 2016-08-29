package br.gov.mec.aghu.patrimonio.action;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioSituacaoAceiteTecnico;
import br.gov.mec.aghu.dominio.DominioTipoProcessoPatrimonio;
import br.gov.mec.aghu.dominio.DominioTipoValorDesmembramento;
import br.gov.mec.aghu.model.PtmAreaTecAvaliacao;
import br.gov.mec.aghu.model.PtmAvaliacaoTecnica;
import br.gov.mec.aghu.model.PtmDesmembramento;
import br.gov.mec.aghu.model.PtmItemRecebProvisorios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoMarcaComercial;
import br.gov.mec.aghu.model.ScoMarcaModelo;
import br.gov.mec.aghu.patrimonio.business.IPatrimonioFacade;
import br.gov.mec.aghu.patrimonio.cadastroapoio.AnexosArquivosListPaginatorController;
import br.gov.mec.aghu.patrimonio.vo.AvaliacaoTecnicaVO;
import br.gov.mec.aghu.patrimonio.vo.DevolucaoBemPermanenteVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

import com.itextpdf.text.DocumentException;

public class RegistrarAceiteTecnicoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3874359922933124480L;
	private static final Log LOG = LogFactory.getLog(RegistrarAceiteTecnicoController.class);
	private static final String EXCECAO_CAPTURADA="Exceção capturada: ";
	private static final String PAGE_PESQUISAR_ACEITE_TECNICO = "patrimonio-registrarAceiteTecnicoList";
	
	private Log getLog() {
		return LOG;
	}
	private static final String PAGE_ANEXOS_ARQUIVOS_LIST = "patrimonio-anexosArquivosList";
	
	@EJB
	private IPatrimonioFacade patrimonioFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private RelatorioImprimirFichaAceiteTecnicoBemPermanente relatorioImprimirFichaAceiteTecnicoBemPermanente;

	private Boolean modoEdicao;
	
	private AvaliacaoTecnicaVO itemSelecionado;
	private AvaliacaoTecnicaVO itemEdicao;
	
	private PtmAvaliacaoTecnica avaliacaoTecnica;
	
	private BigDecimal total;
	private BigDecimal valorSubtr;
	private BigDecimal valorItemDesmembramentoSelecionado; 
	private Short percentualItemDesmembramentoSelecionado;
	private Double valorBem;
	
	private List<DevolucaoBemPermanenteVO> listBensPermantes;
	private List<DevolucaoBemPermanenteVO> bensPermantesSelecionadas;
	private DevolucaoBemPermanenteVO[] itensDevolucaoSelecionados;
	@Inject
	private RegistrarDevolucaoBemPermanenteDataModel devBemPermanDataModel;
	private Boolean allCheck = Boolean.FALSE;
	
	private RapServidores servidor;
	
	private PtmDesmembramento desmembramento;
	private PtmDesmembramento itemDesmembramentoSelecionado;
	private PtmDesmembramento itemDesmembramentoGridSelecionado;
	private List<PtmDesmembramento> listaDesmembramento;
	private Integer indSeqDesmembramento = 0;
	private Boolean modoEdicaoDesmebramento = Boolean.FALSE;
	private Boolean disableBotaoVisualizar; 
	private Boolean disableBotaoFinalizar; 
	private Boolean disableBotaoCertificar; 
	private Boolean disableBotaoGravar; 
	
	private Boolean permissaoGravar; 
	private Boolean permissaoFinalizar; 
	private Boolean permissaoCertificar;
	private Boolean permissaoImprimir;
	private Boolean permissaoChefeAreaTecnicaAvaliacao;
	private Boolean permissaoManter = Boolean.FALSE;
	private Boolean gravou;
	private Boolean campoDescricaoVazio;
	private Boolean campoJustificativaVazio;
	private Boolean campoDescricaoDesmVazio;
	private Boolean campoPercentualVazio;
	private Boolean campoValorVazio;
	private Boolean permissaoEditar;
	private Boolean anexarArquivo = Boolean.FALSE;	
	private boolean chefe = Boolean.FALSE;	
	@Inject
	private AnexosArquivosListPaginatorController anexosArquivosListPaginatorController;
	
	private boolean itemFinalizado;
	private boolean itemCertificado;
	private boolean checarValor = false;
	private boolean checarPercentual = false;
	private Boolean modoVisualizar = Boolean.TRUE;
	
	public enum RegistrarAceiteTecnicoControllerExceptionCode implements BusinessExceptionCode {
		DESM_MAIOR_BEM_PERMAMENTE, PERCENT_DESMEM_NAO_ACEITO, VALOR_DESMEMBRAMENTO_MAIOR_BEM_PERMANENTE, ITEM_NAO_SELECIONADO,
		SUCESSO_GRAVAR_ACEITE, SUCESSO_FINALIZAR_ACEITE, SUCESSO_CERTIFICAR_ACEITE, JUSTIFICATIVA_CAMPO_OBRIGATORIO, DESCRICAO_MATERIAL_CAMPO_OBRIGATORIO,
		CAMPO_OBRIGATORIO_DESCRICAO_DESMEMBRAMENTO, CAMPO_OBRIGATORIO_VALOR, CAMPO_OBRIGATORIO_PERCENTUAL;
	}
	
	@PostConstruct
	public void inicializar() {
		begin(conversation);
	}
	
	public void iniciar(){
		if(modoVisualizar){
			this.avaliacaoTecnica = new PtmAvaliacaoTecnica();
			this.desmembramento = new PtmDesmembramento();
			devBemPermanDataModel = new RegistrarDevolucaoBemPermanenteDataModel();
			this.listBensPermantes = new ArrayList<DevolucaoBemPermanenteVO>();
			this.listaDesmembramento = new ArrayList<PtmDesmembramento>();
			this.bensPermantesSelecionadas = new ArrayList<DevolucaoBemPermanenteVO>();
			this.itensDevolucaoSelecionados = new DevolucaoBemPermanenteVO[0];
			this.itemDesmembramentoSelecionado = new PtmDesmembramento();
			
			this.servidor = servidorLogadoFacade.obterServidorLogado();
			String obterSerivdor = obterLoginUsuarioLogado();
			this.permissaoGravar = this.permissionService.usuarioTemPermissao(obterSerivdor, "gravarAceiteTecnico", "gravar");
			this.permissaoFinalizar = this.permissionService.usuarioTemPermissao(obterSerivdor, "finalizarAceiteTecnico", "finalizar");
			this.permissaoCertificar = this.permissionService.usuarioTemPermissao(obterSerivdor, "certificarAceiteTecnico", "certificar");
			this.permissaoImprimir = this.permissionService.usuarioTemPermissao(obterSerivdor, "imprimirAceiteTecnico", "imprimir");
			this.permissaoChefeAreaTecnicaAvaliacao = this.permissionService.usuarioTemPermissao(obterSerivdor, "chefeAreaTecnicaAvaliacao", "executar");
			if (this.permissaoGravar || this.permissaoFinalizar || this.permissaoCertificar) {
				this.permissaoManter = Boolean.TRUE;
			}
			PtmAreaTecAvaliacao areaCentroCusto = patrimonioFacade.obterAreaTecPorServidor(this.servidor);
			if(areaCentroCusto != null && areaCentroCusto.getSeq() != null){
				this.chefe = patrimonioFacade.verificarChefeParaAreaTecnica(areaCentroCusto.getSeq(), this.servidor);
			}
			
			if(this.modoEdicao != null && this.modoEdicao){
				carregarTela();
				preencherListaBemPermanente();
				checarStatus();
				carregarBotoes();
				preencherItensDesmembramento();
			}else{
				this.avaliacaoTecnica.setIndSituacao(DominioSituacaoAceiteTecnico.N);
				this.allCheck = Boolean.FALSE;
				this.total = null;
				this.valorBem = null;
				this.gravou = Boolean.FALSE;
			}
		}
	}

	private void preencherItensDesmembramento() {
		this.valorBem = this.patrimonioFacade.carregarCampoDaTela(this.avaliacaoTecnica);//C8
		this.listaDesmembramento = this.patrimonioFacade.pesquisarDesmembramentoPorAvtSeq(this.avaliacaoTecnica.getSeq());
		if(listaDesmembramento != null && !listaDesmembramento.isEmpty()){
			this.total = BigDecimal.ZERO;
			for (PtmDesmembramento des : listaDesmembramento) {
				if(des.getValor() != null){
					this.total = this.total.add(des.getValor());
				}else{
					Double valorResult = ((this.valorBem * des.getPercentual()) / 100);
					des.setValor(new BigDecimal(valorResult));
					this.total = this.total.add(des.getValor());
				}
			}
		}
	}
	
	public void checarStatus(){
		if(DominioSituacaoAceiteTecnico.F.equals(avaliacaoTecnica.getIndSituacao())){
			this.itemFinalizado = true;
			this.gravou = true;
		}else if(DominioSituacaoAceiteTecnico.C.equals(avaliacaoTecnica.getIndSituacao())){
			this.itemCertificado = true;
			this.gravou = true;
		}else{
			this.gravou = false;
		}
	}
	
	public void preencherListaBemPermanente(){
        this.listBensPermantes = this.patrimonioFacade.pesquisarBensPermanentesPorSeqPirp(
        		this.avaliacaoTecnica.getItemRecebProvisorio().getSeq(), true, this.avaliacaoTecnica.getSeq());//C9
        devBemPermanDataModel = new RegistrarDevolucaoBemPermanenteDataModel(this.listBensPermantes);
        for (DevolucaoBemPermanenteVO devolucaoBemPermanenteVO : listBensPermantes) {
			if (devolucaoBemPermanenteVO.getAvtSeq() != null) {
				devolucaoBemPermanenteVO.setSelecionado(true);
				bensPermantesSelecionadas.add(devolucaoBemPermanenteVO);
			}
		}
       itensDevolucaoSelecionados = bensPermantesSelecionadas.toArray(new DevolucaoBemPermanenteVO[bensPermantesSelecionadas.size()]);
       if (bensPermantesSelecionadas.size() == listBensPermantes.size()) {
    	   allCheck = true;
       }
	}

	private void carregarBotoes(){
		/*Botão imprimir*/
		if(permissaoImprimir){
			this.disableBotaoVisualizar = Boolean.FALSE;
		}else{
			this.disableBotaoVisualizar = Boolean.TRUE;
		}
		/*Botão Finalizar*/
		if(permissaoFinalizar || !DominioSituacaoAceiteTecnico.F.equals(this.avaliacaoTecnica.getIndSituacao()) ||
				this.gravou || !DominioSituacaoAceiteTecnico.C.equals(this.avaliacaoTecnica.getIndSituacao())){
			this.disableBotaoFinalizar = Boolean.FALSE;
		}else{
			this.disableBotaoFinalizar = Boolean.TRUE;
		}
		/*Botão Certificar*/
		if(permissaoCertificar || !DominioSituacaoAceiteTecnico.C.equals(this.avaliacaoTecnica.getIndSituacao())
				|| this.gravou){
			this.disableBotaoCertificar = Boolean.FALSE;
		}else{
			this.disableBotaoCertificar = Boolean.TRUE;
		}
		/*Botão Gravar*/
		if(permissaoGravar || DominioSituacaoAceiteTecnico.N.equals(this.avaliacaoTecnica.getIndSituacao())){
			this.disableBotaoGravar = Boolean.FALSE;
		}else{
			this.disableBotaoGravar = Boolean.TRUE;
		}
	}
			
	public void carregarTela(){
		this.avaliacaoTecnica = this.patrimonioFacade.obterAvaliacaoTecnicaPorSeq(this.itemEdicao.getSeqAvaliacaoTec());
		
		if (this.avaliacaoTecnica.getMcmCod() != null) {
			this.avaliacaoTecnica.setMarcaComercial(comprasFacade
					.obterMarcaComercialPorCodigo(this.avaliacaoTecnica.getMcmCod()));
			if (this.avaliacaoTecnica.getMomCod() != null) {
				this.avaliacaoTecnica.setMarcaModelo(comprasFacade
						.buscaScoMarcaModeloPorId(this.avaliacaoTecnica.getMomCod(),
								this.avaliacaoTecnica.getMcmCod()));
			}
		}
		this.avaliacaoTecnica.setItemRecebProvisorio(patrimonioFacade.pesquisarItemRecebSeq(this.itemEdicao.getSceNrpSeq(), this.itemEdicao.getSceNrpItem()));
	}
	
	public void carregarConsultasPosteriores(){
		this.allCheck = Boolean.FALSE;
		this.bensPermantesSelecionadas = new ArrayList<DevolucaoBemPermanenteVO>();
		if (this.avaliacaoTecnica.getItemRecebProvisorio() != null) {
			this.valorBem = this.patrimonioFacade.carregarCampoDaTela(this.avaliacaoTecnica);//C8
			if (this.avaliacaoTecnica.getItemRecebProvisorio().getSeq() != null) {
				this.listBensPermantes = this.patrimonioFacade.pesquisarBensPermanentesPorSeqPirp(this.avaliacaoTecnica.getItemRecebProvisorio().getSeq(), false, null);//C9
				devBemPermanDataModel = new RegistrarDevolucaoBemPermanenteDataModel(this.listBensPermantes);
			}
		}
	}
	
	public void limparCampos(){
		this.listaDesmembramento = new ArrayList<PtmDesmembramento>();
		this.listBensPermantes = new ArrayList<DevolucaoBemPermanenteVO>();
		this.devBemPermanDataModel = new RegistrarDevolucaoBemPermanenteDataModel(this.listBensPermantes);
		this.itemEdicao = new AvaliacaoTecnicaVO();
		this.allCheck = Boolean.FALSE;
		this.itemFinalizado = false;
		this.itemCertificado = false;
		this.gravou = false;
		this.bensPermantesSelecionadas = new ArrayList<DevolucaoBemPermanenteVO>();
		if (this.avaliacaoTecnica != null && DominioSituacaoAceiteTecnico.C.equals(this.avaliacaoTecnica.getIndSituacao())) {
			this.avaliacaoTecnica = new PtmAvaliacaoTecnica();
			this.avaliacaoTecnica.setIndSituacao(DominioSituacaoAceiteTecnico.C);
		}else if (this.avaliacaoTecnica != null && DominioSituacaoAceiteTecnico.F.equals(this.avaliacaoTecnica.getIndSituacao())){
			this.avaliacaoTecnica = new PtmAvaliacaoTecnica();
			this.avaliacaoTecnica.setIndSituacao(DominioSituacaoAceiteTecnico.F);
		}else{
			this.avaliacaoTecnica = new PtmAvaliacaoTecnica();
			this.avaliacaoTecnica.setIndSituacao(DominioSituacaoAceiteTecnico.N);
		}
		this.valorBem = null;
		this.total = null;
		this.checarValor = false;
		this.checarPercentual = false;
		carregarBotoes();
	}
	
	public void adicionarDesmembramento(){
		this.permissaoEditar = Boolean.TRUE;
		try {
			if (validarCamposDesmembramentos()){return;	}
			
			if (this.desmembramento.getPercentual() != null && this.desmembramento.getPercentual() > 100) {
				apresentarMsgNegocio(Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.PERCENT_DESMEM_NAO_ACEITO.toString());
				return;
			}
			
			if (this.total == null) {this.total = BigDecimal.ZERO;
			}
			if (this.valorBem != null) {
				if(modoEdicaoDesmebramento && this.valorSubtr != null){
					this.total = this.total.subtract(this.valorSubtr);
				}
				
				if (this.desmembramento.getValor() != null && !modoEdicaoDesmebramento ||
					modoEdicaoDesmebramento && this.desmembramento.getTipoValor() != null && DominioTipoValorDesmembramento.V.equals(this.desmembramento.getTipoValor())) {
					this.desmembramento.setTipoValor(DominioTipoValorDesmembramento.V);
					BigDecimal percentualResult = this.desmembramento.getValor().multiply(new BigDecimal(100))
							.divide(new BigDecimal(this.valorBem), RoundingMode.HALF_DOWN);
					this.desmembramento.setPercentual(percentualResult.shortValue());
					this.total = this.total.add(this.desmembramento.getValor());
					if (this.valorBem < this.total.doubleValue()) {
						this.total = this.total.subtract(this.desmembramento.getValor());
						desmembramento.setPercentual(null);
						desmembramento.setValor(null);
						apresentarMsgNegocio(Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.DESM_MAIOR_BEM_PERMAMENTE.toString());
						return;
					}
				}else{
					this.desmembramento.setTipoValor(DominioTipoValorDesmembramento.P);
					Double valorResult = ((this.valorBem * this.desmembramento.getPercentual()) / 100);
					this.desmembramento.setValor(new BigDecimal(valorResult));
					this.total = this.total.add(this.desmembramento.getValor());
					if (this.valorBem < this.total.doubleValue()) {
						this.total = this.total.subtract(this.desmembramento.getValor());
						desmembramento.setPercentual(null);
						desmembramento.setValor(null);
						apresentarMsgNegocio(Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.DESM_MAIOR_BEM_PERMAMENTE.toString());
						return;
					}
				}
				Double resultado = this.valorBem - this.total.doubleValue();
				if (resultado < 0) {
					apresentarMsgNegocio(Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.VALOR_DESMEMBRAMENTO_MAIOR_BEM_PERMANENTE.toString());
					return;
				}
			}
			alterarDesmebramento();
			this.desmembramento =  new PtmDesmembramento();
		} catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
		}
		
	}

	private void alterarDesmebramento() {
		if(this.modoEdicaoDesmebramento){
			for (PtmDesmembramento elemento : listaDesmembramento) {
				if(elemento.getSeq().equals(this.desmembramento.getSeq())){
					listaDesmembramento.remove(elemento);
					this.desmembramento.setServidor(this.servidor);
					listaDesmembramento.add(this.desmembramento);
					break;
				}
			}
			this.modoEdicaoDesmebramento = Boolean.FALSE;
		}else{
			indSeqDesmembramento--;
			this.desmembramento.setSeq(indSeqDesmembramento);
			this.desmembramento.setServidor(this.servidor);
			listaDesmembramento.add(this.desmembramento);
		}
	}

	private boolean validarCamposDesmembramentos() {
		
		verificarCampoValor();
		verificarCampoPercentual();
		
		boolean validarMsg = false;
		if(this.desmembramento.getDescricao() == null || this.desmembramento.getDescricao().trim().isEmpty()){
			this.campoDescricaoDesmVazio = true;
			apresentarMsgNegocio("inputDescricao", Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.CAMPO_OBRIGATORIO_DESCRICAO_DESMEMBRAMENTO.toString());
			validarMsg = true;
		} 
		
		if((this.desmembramento.getPercentual() == null && this.checarPercentual && this.checarValor) ||
				(this.desmembramento.getPercentual() == null && this.desmembramento.getValor() == null)){
			this.campoPercentualVazio = true;
			
			apresentarMsgNegocio("inputPercentual", Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.CAMPO_OBRIGATORIO_PERCENTUAL.toString());
			validarMsg = true;
		}
		if((this.desmembramento.getValor() == null && this.checarValor && this.checarPercentual) || 
				(this.desmembramento.getValor() == null && this.desmembramento.getPercentual() == null)){
			this.campoValorVazio = true;
			apresentarMsgNegocio("inputValor", Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.CAMPO_OBRIGATORIO_VALOR.toString());
			validarMsg = true;
		}
		return validarMsg;
	}
	
	public void preAlterarDesmembramento() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException{
		this.modoEdicaoDesmebramento = Boolean.TRUE;
		this.permissaoEditar = Boolean.FALSE;
		this.valorSubtr = itemDesmembramentoSelecionado.getValor();
		PropertyUtils.copyProperties(this.desmembramento, this.itemDesmembramentoSelecionado);
		if(DominioTipoValorDesmembramento.V.equals(this.itemDesmembramentoSelecionado.getTipoValor())){
			setValorItemDesmembramentoSelecionado(this.itemDesmembramentoSelecionado.getValor());
		}else{
			setPercentualItemDesmembramentoSelecionado(this.itemDesmembramentoSelecionado.getPercentual());
		}
	}

	public void verificarCampoValor(){
		if (desmembramento.getPercentual() != null){
			checarValor = true;
			checarPercentual = false;
		}
	}
	public void verificarCampoPercentual(){
		if (desmembramento.getValor() != null){
			checarPercentual = true;
			checarValor = false;
		}
	}
		
	public void preExcluirDesmembramento(){
		super.openDialog("modalExclusaoDesmembramentoWG");
	}
	
	public void excluirDesmembramento(){
		if(this.itemDesmembramentoSelecionado.getSeq() != null && this.itemDesmembramentoSelecionado.getSeq() > 0){
			this.patrimonioFacade.excluirDesmembramento(this.itemDesmembramentoSelecionado.getSeq());
		}
		if (this.itemDesmembramentoSelecionado.getValor() != null) {
			this.total = this.total.subtract(this.itemDesmembramentoSelecionado.getValor());
		}else{
			Double valorResult = ((this.valorBem * this.itemDesmembramentoSelecionado.getPercentual()) / 100);
			this.total = this.total.subtract(new BigDecimal(valorResult));
		}
		this.listaDesmembramento.remove(itemDesmembramentoSelecionado);
		this.itemDesmembramentoSelecionado = new PtmDesmembramento();
		super.closeDialog("modalExclusaoDesmembramentoWG");
	}
	
	public void cancelarAlteracao(){
		this.modoEdicaoDesmebramento = Boolean.FALSE;
		this.permissaoEditar = Boolean.TRUE;
		setDesmembramento(new PtmDesmembramento());
		setItemDesmembramentoSelecionado(new PtmDesmembramento());
	}
	
	public void gravar(){
		try{
			this.patrimonioFacade.registrarAceiteTecnico(this.avaliacaoTecnica, this.listaDesmembramento, this.listBensPermantes, this.itensDevolucaoSelecionados, this.servidor);
			this.gravou = Boolean.TRUE;
			preencherListaBemPermanente();
			checarStatus();
			carregarBotoes();
			apresentarMsgNegocio(Severity.INFO, RegistrarAceiteTecnicoControllerExceptionCode.SUCESSO_GRAVAR_ACEITE.toString());
			this.anexarArquivo = true;
		}catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
		}
	}
	
	public void finalizar(){
		try{
			if (verificarItemBemDevolucaoSelecionado()){
				return;
			}
			if(verificarCamposDescricaoJustificativa()){
				return;
			}
			this.patrimonioFacade.finalizarAceiteTecnico(this.avaliacaoTecnica, this.listaDesmembramento, this.listBensPermantes, this.itensDevolucaoSelecionados, this.servidor);
			preencherListaBemPermanente();
			checarStatus();
			carregarBotoes();
			apresentarMsgNegocio(Severity.INFO, RegistrarAceiteTecnicoControllerExceptionCode.SUCESSO_FINALIZAR_ACEITE.toString());
		}catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
		}
	}
	
	public String certificar(){
		try{
			if (verificarItemBemDevolucaoSelecionado()){
				return null;
			}
			if(verificarCamposDescricaoJustificativa()){
				return null;
			}
		this.patrimonioFacade.certificarAceiteTecnico(this.avaliacaoTecnica, this.listaDesmembramento, this.listBensPermantes, this.itensDevolucaoSelecionados, this.servidor);
		preencherListaBemPermanente();
		checarStatus();
		carregarBotoes();
		apresentarMsgNegocio(Severity.INFO, RegistrarAceiteTecnicoControllerExceptionCode.SUCESSO_CERTIFICAR_ACEITE.toString());
		return visualizarImpressao();
		}catch (Exception e) {
			getLog().error(EXCECAO_CAPTURADA, e);
		}
		return null;
	}

	private boolean verificarItemBemDevolucaoSelecionado() {
		if(itensDevolucaoSelecionados != null && itensDevolucaoSelecionados.length <= 0){
			apresentarMsgNegocio(Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.ITEM_NAO_SELECIONADO.toString());
			return true;
		}else{
			return false;
		}
	}

	private boolean verificarCamposDescricaoJustificativa() {
		if(DominioAceiteTecnico.R.equals(avaliacaoTecnica.getIndStatus())){
			if(avaliacaoTecnica.getJustificativa().trim().isEmpty()){
				this.campoJustificativaVazio = Boolean.TRUE;
				apresentarMsgNegocio("textAreaJustificativa", Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.JUSTIFICATIVA_CAMPO_OBRIGATORIO.toString());
				return campoJustificativaVazio;
			}
		}else if(DominioAceiteTecnico.A.equals(avaliacaoTecnica.getIndStatus())){
			if(avaliacaoTecnica.getDescricaoMaterial().trim().isEmpty()){
				this.campoDescricaoVazio = Boolean.TRUE;
				apresentarMsgNegocio("textAreaDescricaoMaterial", Severity.ERROR, RegistrarAceiteTecnicoControllerExceptionCode.DESCRICAO_MATERIAL_CAMPO_OBRIGATORIO.toString());
				return campoDescricaoVazio;
			}
		}
		return false;
	}

	public String visualizarImpressao(){
		try {
			if(this.avaliacaoTecnica != null && this.avaliacaoTecnica.getSeq() == null){
				throw new ApplicationBusinessException("ERRO_VISUALIZAR_RELATORIO", null);
			}
			return relatorioImprimirFichaAceiteTecnicoBemPermanente.visualizar(avaliacaoTecnica.getSeq());
		} catch (ApplicationBusinessException | JRException | IOException| DocumentException e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_VISUALIZAR_RELATORIO");
			return null;
		}
	}
	
	public String voltar(){
		limparCampos();
		this.anexarArquivo = false;
		return PAGE_PESQUISAR_ACEITE_TECNICO;
	}
	
	//MARCA DESMARCA TODOS
		public void checkAll(Boolean allCheck2){
			if(allCheck){
				for(DevolucaoBemPermanenteVO vo: listBensPermantes){
					this.bensPermantesSelecionadas.add(vo);
					vo.setSelecionado(Boolean.TRUE);
				}
			}
			else{
				this.bensPermantesSelecionadas = new ArrayList<DevolucaoBemPermanenteVO>();
			}
		}	
		
		//MARCA DESMARCA 1
		public void checkItem(DevolucaoBemPermanenteVO item){
			if(bensPermantesSelecionadas.contains(item)){
				bensPermantesSelecionadas.remove(item);
				allCheck = Boolean.FALSE;
			}else{
				bensPermantesSelecionadas.add(item);
			}
			alterarSelecaoNaListaVO();
		}
		
		//ALTERA A SELECAO NA LISTA
		private void alterarSelecaoNaListaVO(){
			for(DevolucaoBemPermanenteVO vo: listBensPermantes){
				if(bensPermantesSelecionadas.contains(vo)){
					vo.setSelecionado(true);
				}else{
					vo.setSelecionado(false);
				}
			}
			if (listBensPermantes != null && !listBensPermantes.isEmpty() && bensPermantesSelecionadas != null && !bensPermantesSelecionadas.isEmpty()) {
				if (listBensPermantes.size() == bensPermantesSelecionadas.size()) {
					allCheck = Boolean.TRUE;
					return;
				}
			}
			allCheck = Boolean.FALSE;
		}
		
	/**
	 * Metodo para atualizar o SuggetionBox.
	 */
	public void refreshFromMarca() {
		if(this.avaliacaoTecnica.getMarcaComercial() == null){
			this.avaliacaoTecnica.setMarcaModelo(null);
		}
	}
		
	/**
	 * Suggestion Box de Material.
	 */
	public List<PtmItemRecebProvisorios> listarItemRecebimento(String parametro) {
		return returnSGWithCount(this.patrimonioFacade.listarPatrimonioPorItemReceb(parametro, servidor),
				this.patrimonioFacade.listarPatrimonioPorItemRecebCount(parametro, servidor));
	}
	public List<ScoMarcaComercial> listarMarcaComercialPorCodigoDescricao(String param) {
		return returnSGWithCount(this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoSemLucene(param), 
				this.comprasFacade.pesquisarMarcaComercialPorCodigoDescricaoCount(param));
	}
	
	public List<ScoMarcaModelo> listarMarcaModeloPorCodigoDescricao(String param){
		return returnSGWithCount(this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoSemLucene(param, this.avaliacaoTecnica.getMarcaComercial(), null),
				this.comprasFacade.pesquisarMarcaModeloPorCodigoDescricaoCount(param, this.avaliacaoTecnica.getMarcaComercial(), null));
	}
	
	public String anexarArquivos(){
		this.anexosArquivosListPaginatorController.getArquivosAnexosPesquisaFiltroVO().setAceiteTecnico(this.avaliacaoTecnica.getSeq());
		this.anexosArquivosListPaginatorController.getArquivosAnexosPesquisaFiltroVO().setRecebimentoItem(this.avaliacaoTecnica.getItemRecebProvisorio());
		this.anexosArquivosListPaginatorController.getArquivosAnexosPesquisaFiltroVO().setTipoProcesso(DominioTipoProcessoPatrimonio.ACEITE_TECNICO);
		this.anexosArquivosListPaginatorController.setRegistroAceiteTecnico(true);
		return PAGE_ANEXOS_ARQUIVOS_LIST;
	}
	
	//GETTERS AND SETTERS
	public boolean isModoEdicao() {
		return modoEdicao;
	}
	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	public PtmAvaliacaoTecnica getAvaliacaoTecnica() {
		return avaliacaoTecnica;
	}
	public void setAvaliacaoTecnica(PtmAvaliacaoTecnica avaliacaoTecnica) {
		this.avaliacaoTecnica = avaliacaoTecnica;
	}
	public AvaliacaoTecnicaVO getItemSelecionado() {
		return itemSelecionado;
	}
	public void setItemSelecionado(AvaliacaoTecnicaVO itemSelecionado) {
		this.itemSelecionado = itemSelecionado;
	}
	public AvaliacaoTecnicaVO getItemEdicao() {
		return itemEdicao;
	}
	public void setItemEdicao(AvaliacaoTecnicaVO itemEdicao) {
		this.itemEdicao = itemEdicao;
	}
	public PtmDesmembramento getDesmembramento() {
		return desmembramento;
	}
	public void setDesmembramento(PtmDesmembramento desmembramento) {
		this.desmembramento = desmembramento;
	}
	public PtmDesmembramento getItemDesmembramentoSelecionado() {
		return itemDesmembramentoSelecionado;
	}
	public void setItemDesmembramentoSelecionado(PtmDesmembramento itemDesmembramentoSelecionado) {
		this.itemDesmembramentoSelecionado = itemDesmembramentoSelecionado;
	}
	public PtmDesmembramento getItemDesmembramentoGridSelecionado() {
		return itemDesmembramentoGridSelecionado;
	}
	public void setItemDesmembramentoGridSelecionado(PtmDesmembramento itemDesmembramentoGridSelecionado) {
		this.itemDesmembramentoGridSelecionado = itemDesmembramentoGridSelecionado;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Double getValorBem() {
		return valorBem;
	}
	public void setValorBem(Double valorBem) {
		this.valorBem = valorBem;
	}
	public List<DevolucaoBemPermanenteVO> getListBensPermantes() {
		return listBensPermantes;
	}
	public void setListBensPermantes(
			List<DevolucaoBemPermanenteVO> listBensPermantes) {
		this.listBensPermantes = listBensPermantes;
	}
	public Boolean getAllCheck() {
		return allCheck;
	}
	public void setAllCheck(Boolean allCheck) {
		this.allCheck = allCheck;
	}
	public List<PtmDesmembramento> getListaDesmembramento() {
		return listaDesmembramento;
	}
	public void setListaDesmembramento(List<PtmDesmembramento> listaDesmembramento) {
		this.listaDesmembramento = listaDesmembramento;
	}
	public DevolucaoBemPermanenteVO[] getItensDevolucaoSelecionados() {
		return itensDevolucaoSelecionados;
	}
	public void setItensDevolucaoSelecionados(DevolucaoBemPermanenteVO[] itensDevolucaoSelecionados) {
		this.itensDevolucaoSelecionados = itensDevolucaoSelecionados;
	}
	public RegistrarDevolucaoBemPermanenteDataModel getDevBemPermanDataModel() {
		return devBemPermanDataModel;
	}
	public void setDevBemPermanDataModel(RegistrarDevolucaoBemPermanenteDataModel devBemPermanDataModel) {
		this.devBemPermanDataModel = devBemPermanDataModel;
	}
	public Boolean getModoEdicaoDesmebramento() {
		return modoEdicaoDesmebramento;
	}
	public void setModoEdicaoDesmebramento(Boolean modoEdicaoDesmebramento) {
		this.modoEdicaoDesmebramento = modoEdicaoDesmebramento;
	}
	public Boolean getModoEdicao() {
		return modoEdicao;
	}
	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}
	public Boolean getPermissaoGravar() {
		return permissaoGravar;
	}
	public void setPermissaoGravar(Boolean permissaoGravar) {
		this.permissaoGravar = permissaoGravar;
	}
	public Boolean getPermissaoFinalizar() {
		return permissaoFinalizar;
	}
	public void setPermissaoFinalizar(Boolean permissaoFinalizar) {
		this.permissaoFinalizar = permissaoFinalizar;
	}
	public Boolean getPermissaoCertificar() {
		return permissaoCertificar;
	}
	public void setPermissaoCertificar(Boolean permissaoCertificar) {
		this.permissaoCertificar = permissaoCertificar;
	}
	public Boolean getPermissaoManter() {
		return permissaoManter;
	}
	public void setPermissaoManter(Boolean permissaoManter) {
		this.permissaoManter = permissaoManter;
	}
	public Boolean getGravou() {
		return gravou;
	}
	public void setGravou(Boolean gravou) {
		this.gravou = gravou;
	}
	public Boolean getCampoDescricaoVazio() {
		return campoDescricaoVazio;
	}
	public void setCampoDescricaoVazio(Boolean campoDescricaoVazio) {
		this.campoDescricaoVazio = campoDescricaoVazio;
	}
	public Boolean getCampoJustificativaVazio() {
		return campoJustificativaVazio;
	}
	public void setCampoJustificativaVazio(Boolean campoJustificativaVazio) {
		this.campoJustificativaVazio = campoJustificativaVazio;
	}
	public Boolean getCampoDescricaoDesmVazio() {
		return campoDescricaoDesmVazio;
	}
	public void setCampoDescricaoDesmVazio(Boolean campoDescricaoDesmVazio) {
		this.campoDescricaoDesmVazio = campoDescricaoDesmVazio;
	}
	public Boolean getCampoPercentualVazio() {
		return campoPercentualVazio;
	}
	public void setCampoPercentualVazio(Boolean campoPercentualVazio) {
		this.campoPercentualVazio = campoPercentualVazio;
	}
	public Boolean getCampoValorVazio() {
		return campoValorVazio;
	}
	public void setCampoValorVazio(Boolean campoValorVazio) {
		this.campoValorVazio = campoValorVazio;
	}
	public Boolean getPermissaoEditar() {
		return permissaoEditar;
	}
	public void setPermissaoEditar(Boolean permissaoEditar) {
		this.permissaoEditar = permissaoEditar;
	}
	public boolean isItemFinalizado() {
		return itemFinalizado;
	}
	public void setItemFinalizado(boolean itemFinalizado) {
		this.itemFinalizado = itemFinalizado;
	}
	public boolean isItemCertificado() {
		return itemCertificado;
	}
	public void setItemCertificado(boolean itemCertificado) {
		this.itemCertificado = itemCertificado;
	}
	public Boolean getPermissaoImprimir() {
		return permissaoImprimir;
	}
	public void setPermissaoImprimir(Boolean permissaoImprimir) {
		this.permissaoImprimir = permissaoImprimir;
	}
	public Boolean getPermissaoChefeAreaTecnicaAvaliacao() {
		return permissaoChefeAreaTecnicaAvaliacao;
	}
	public void setPermissaoChefeAreaTecnicaAvaliacao(Boolean permissaoChefeAreaTecnicaAvaliacao) {
		this.permissaoChefeAreaTecnicaAvaliacao = permissaoChefeAreaTecnicaAvaliacao;
	}
	public Boolean getDisableBotaoVisualizar() {
		return disableBotaoVisualizar;
	}
	public void setDisableBotaoVisualizar(Boolean disableBotaoVisualizar) {
		this.disableBotaoVisualizar = disableBotaoVisualizar;
	}
	public Boolean getDisableBotaoFinalizar() {
		return disableBotaoFinalizar;
	}
	public void setDisableBotaoFinalizar(Boolean disableBotaoFinalizar) {
		this.disableBotaoFinalizar = disableBotaoFinalizar;
	}
	public Boolean getDisableBotaoCertificar() {
		return disableBotaoCertificar;
	}
	public void setDisableBotaoCertificar(Boolean disableBotaoCertificar) {
		this.disableBotaoCertificar = disableBotaoCertificar;
	}
	public Boolean getDisableBotaoGravar() {
		return disableBotaoGravar;
	}
	public void setDisableBotaoGravar(Boolean disableBotaoGravar) {
		this.disableBotaoGravar = disableBotaoGravar;
	}
	public Boolean getAnexarArquivo() {
		return anexarArquivo;
	}
	public void setAnexarArquivo(Boolean anexarArquivo) {
		this.anexarArquivo = anexarArquivo;
	}
	public AnexosArquivosListPaginatorController getAnexosArquivosListPaginatorController() {
		return anexosArquivosListPaginatorController;
	}
	public void setAnexosArquivosListPaginatorController(
			AnexosArquivosListPaginatorController anexosArquivosListPaginatorController) {
		this.anexosArquivosListPaginatorController = anexosArquivosListPaginatorController;
	}
	public boolean isChecarValor() {
		return checarValor;
	}
	public void setChecarValor(boolean checarValor) {
		this.checarValor = checarValor;
	}
	public boolean isChecarPercentual() {
		return checarPercentual;
	}
	public void setChecarPercentual(boolean checarPercentual) {
		this.checarPercentual = checarPercentual;
	}
	public boolean isChefe() {
		return chefe;
	}
	public void setChefe(boolean chefe) {
		this.chefe = chefe;
	}
	public Boolean getModoVisualizar() {
		return modoVisualizar;
	}
	public void setModoVisualizar(Boolean modoVisualizar) {
		this.modoVisualizar = modoVisualizar;
	}

	public BigDecimal getValorItemDesmembramentoSelecionado() {
		return valorItemDesmembramentoSelecionado;
	}

	public void setValorItemDesmembramentoSelecionado(
			BigDecimal valorItemDesmembramentoSelecionado) {
		this.valorItemDesmembramentoSelecionado = valorItemDesmembramentoSelecionado;
	}

	public Short getPercentualItemDesmembramentoSelecionado() {
		return percentualItemDesmembramentoSelecionado;
	}

	public void setPercentualItemDesmembramentoSelecionado(
			Short percentualItemDesmembramentoSelecionado) {
		this.percentualItemDesmembramentoSelecionado = percentualItemDesmembramentoSelecionado;
	}
	
}
