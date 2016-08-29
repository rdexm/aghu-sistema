package br.gov.mec.aghu.estoque.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueBeanFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemPacoteMateriaisVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemTransferencia;
import br.gov.mec.aghu.model.SceItemTransferenciaId;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.model.SceTransferencia;
import br.gov.mec.aghu.model.ScoClassifMatNiv5;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;


public class EfetivarTransfMateriaisEventualController extends ActionController {


	private static final Log LOG = LogFactory.getLog(EfetivarTransfMateriaisEventualController.class);

	private static final long serialVersionUID = -2637057510813289846L;

	private static final String EFETIVAR_TRANSFERENCIA_MATERIAIS_EVENTUAL = "estoque-efetivarTransferenciaMateriaisEventual";

	private static final String PESQUISAR_ESTOQUE_ALMOXARIFADO = "estoque-pesquisarEstoqueAlmoxarifado";

	private static final String MATERIAL_BLOQUEIO_DESBLOQUEIO = "estoque-materialBloqueioDesbloqueio";

	private static final String PESQUISAR_MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA = "estoque-pesquisarMaterialBloqueioDesbloqueioProblema";

	private ScePacoteMateriais pacoteMaterial;

	private SceTransferencia transferencia;

	private String voltarPara;

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;

	// Instância da lista de itens de tranferência
	private List<SceItemTransferencia> listaItensTranferencia = new ArrayList<SceItemTransferencia>();

	private SceItemTransferencia itemTransferencia;

	/*variáveis usadas para alterar/excluir os itens de Transferência*/
	private Integer ealSeq;
	private Integer trfSeq;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IEstoqueBeanFacade estoqueBeanFacade;
	
	/**
	 * Habilita o botão gravar
	 */
	private Boolean habilitaBotao = Boolean.FALSE; 
	private Boolean habilitaBotaoGravar = Boolean.TRUE; 
	private Boolean habilitaBotaoAlterar = Boolean.FALSE; 
	private Boolean emEdicao;
	// Campos de filtro para pesquisa
	//private Integer numero;
	private SceAlmoxarifado almoxarifadoOrigem;
	private SceAlmoxarifado almoxarifadoDestino;
	private Boolean efetivado;
	private Boolean estornado;
	private Date dtGeracao;
	private Date dtEfetivacao;
	private Boolean bloquearMaterial;



	/* Parâmetros impressão após efetivar */

	@Inject
	private RelatorioTransferenciaMaterialController relatorioTransferenciaMaterialController;

	/**
	 * Material a ser inserido na lista
	 */
	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;

	/**
	 * Quantidade enviada a ser inserida na lista
	 */
	private Integer qtdeEnviada;
	
	private SceItemTransferenciaId id;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	
	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio()throws ApplicationBusinessException {
	 

	 


		if (this.trfSeq != null && emEdicao){
			populaCampos();
			this.pesquisar();

		}else{			
			this.limparFiltros();
		}
	
	}
	

	/**
	 * Grava os itens do pacote
	 */
	public void adicionarPacote() {

		List<ItemPacoteMateriaisVO> listItensPacoteMaterial =null;
		try {

			if(pacoteMaterial!=null){

				listItensPacoteMaterial = this.estoqueFacade.pesquisarItensTrsEventualPacoteMateriaisVO(pacoteMaterial, almoxarifadoDestino.getSeq());

				Boolean novaTransferencia = Boolean.FALSE;
				
				if (listItensPacoteMaterial != null && !listItensPacoteMaterial.isEmpty()) {

					if(transferencia == null){
						this.gerarTransferencia();//Grava a Transferencia.
						novaTransferencia = Boolean.TRUE;
					}

					//BUSCA_CLASSIF_MAT
					ScoMaterial material = this.comprasFacade.obterMaterialPorId(listItensPacoteMaterial.get(0).getCodigoMaterial());

					Long codigoClassMat = material.getGrupoMaterial().getCodigo() * Long.valueOf("10000000000");
					final ScoClassifMatNiv5 classifMatNiv5 = this.comprasFacade.obterScoClassifMatNiv5PorSeq(codigoClassMat);
					
					if(classifMatNiv5!=null){
						transferencia.setClassifMatNiv5(classifMatNiv5);
					}

					estoqueFacade.inserirItemTransferenciaEventual(listItensPacoteMaterial,transferencia);

					if(novaTransferencia){
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_TRANSFERENCIA", transferencia.getSeq());
					}else{
						apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INCLUSAO_ITENS_PACOTE");
					}

					this.emEdicao = Boolean.TRUE;
					this.inicio();

				} else {
					apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_ERRO_INCLUSAO_ITENS_PACOTE");
				
				}
			}

		} catch (BaseException e) {
			listItensPacoteMaterial = null;
			apresentarExcecaoNegocio(e);
		}
	}


	private void limparFiltros() {
		this.trfSeq = null;
		this.listaItensTranferencia = null;
		this.transferencia = null;
		this.almoxarifadoOrigem = null;
		this.almoxarifadoDestino = null;
		this.efetivado = null;
		this.estornado = null;
		this.dtGeracao = null;
		this.dtEfetivacao = null;
		this.qtdeEnviada =  null;
		this.estoqueAlmoxarifado = null;
		this.pacoteMaterial = null;
		this.habilitaBotao = Boolean.FALSE;
		this.habilitaBotaoGravar = Boolean.TRUE;
		this.bloquearMaterial = null;
	}

	private void populaCampos() {

		//Consulta transfrência através do id
		this.transferencia = this.estoqueFacade.obterTransferenciaPorId(this.trfSeq);
		this.almoxarifadoOrigem = transferencia.getAlmoxarifado();
		this.almoxarifadoDestino = transferencia.getAlmoxarifadoRecebimento();
		this.efetivado = transferencia.getEfetivada();
		this.bloquearMaterial = transferencia.getEfetivada();
		this.estornado = transferencia.getEstorno();
		this.dtEfetivacao = transferencia.getDtEfetivacao();
		this.dtGeracao = transferencia.getDtGeracao();

	}

	private void pesquisar() {

		if (this.transferencia != null) {

			this.listaItensTranferencia = this.estoqueFacade.pesquisarListaItensTransferenciaEventual(transferencia);

			//Seleciona o primeiro item da lista de itens de transferência
			if (this.listaItensTranferencia != null && !this.listaItensTranferencia.isEmpty()){
				this.itemTransferencia = this.listaItensTranferencia.get(0);
			}

		}
	}


	public void editar(){
		
		if(this.ealSeq !=null && trfSeq!=null){
			this.bloquearMaterial = Boolean.TRUE;
			habilitaBotao = Boolean.TRUE;
			habilitaBotaoGravar = Boolean.FALSE;
			itemTransferencia = this.estoqueFacade.obterItemTransferenciaPorChave(ealSeq, trfSeq);
			this.qtdeEnviada =  itemTransferencia.getQuantidade();
			this.estoqueAlmoxarifado = itemTransferencia.getEstoqueAlmoxarifado();
			
			setHabilitaBotaoAlterar(Boolean.TRUE);

		}

	}

	/**
	 * Grava a transferencia e itens
	 */
	public void alterar() {

		try {
			
			if (getQtdeEnviada() == null || this.estoqueAlmoxarifado == null) {
				habilitaBotao = Boolean.TRUE;
				habilitaBotaoGravar = Boolean.FALSE;
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_ADD_ITENS");
		
			} else {

				if (itemTransferencia != null) {
				
					itemTransferencia.setQtdeEnviada(this.qtdeEnviada);
					itemTransferencia.setQuantidade(this.qtdeEnviada);

					itemTransferencia.setEstoqueAlmoxarifado(this.estoqueAlmoxarifado);
					String nomeMicrocomputador = null;
					try {
						nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
					} catch (UnknownHostException e) {
						LOG.error("Exceção caputada:", e);
					}
					
					this.estoqueFacade.atualizarItemTransfEventual(itemTransferencia, nomeMicrocomputador);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ALTERAR_TRANSF_EVENTUAL");
					this.cancelarEdicao();
					setHabilitaBotaoAlterar(Boolean.FALSE);
					this.inicio();
				}
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}


	/**
	 * Efetiva Transferência Eventual
	 */
	public void efetivar(){

		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Exceção caputada:", e);
		}
		
		try {
			this.transferencia.setEfetivada(Boolean.TRUE);
			this.estoqueBeanFacade.efetivarTransferenciaAutoAlmoxarifados(this.transferencia, nomeMicrocomputador);

			relatorioTransferenciaMaterialController.setNumTransferenciaMaterial(this.transferencia.getSeq());
			relatorioTransferenciaMaterialController.setOrigem(EFETIVAR_TRANSFERENCIA_MATERIAIS_EVENTUAL);
			if(this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_LIMITAR_IMRESSAO_DE_VIAS).getVlrTexto().equalsIgnoreCase("N")){

				relatorioTransferenciaMaterialController.setIndImprime2Vias(Boolean.TRUE);

			}

			relatorioTransferenciaMaterialController.impressaoDireta();

			this.inicio();

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EFETIVAR_TRANSF_EVENTUAL");

		} catch (BaseException e) {
			
			super.apresentarExcecaoNegocio(e);
			
		}
	}

	/**
	 * Remove um item de transferência selecionado na lista
	 */
	public void excluir() {

		try {

			if(this.id!=null){

				if(this.listaItensTranferencia.size()==1){
					apresentarMsgNegocio(Severity.WARN,"MENSAGEM_ERRO_REMOVER_ULTIMO_ITEM");
					
				}else{
					String nomeMaterial = estoqueFacade.obterNomeMaterialItemTransferencia(id.getEalSeq(), id.getTrfSeq());
					this.estoqueBeanFacade.removerItemTransferenciaAutoAlmoxarifado(id.getEalSeq(), id.getTrfSeq());
					this.inicio();

					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOVER_ITEM_TRANSFERENCIA", nomeMaterial);
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_REMOVER_ITEM_TRANSFERENCIA");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método chamado para o botão voltar
	 */
	public void cancelarEdicao() {
		this.qtdeEnviada =  null;
		this.estoqueAlmoxarifado = null;
		this.habilitaBotao = Boolean.FALSE;
		this.habilitaBotaoGravar = Boolean.TRUE;
		setHabilitaBotaoAlterar(Boolean.FALSE);
		this.bloquearMaterial = Boolean.FALSE;
	}


	/**
	 * Método chamado para o botão voltar
	 */
	public String cancelar() {
		this.limparFiltros();
		return this.voltarPara;
	}

	/**
	 * Adiciona item na lista
	 */
	public void adicionarItens() {

		try {

			if (getQtdeEnviada() == null || this.estoqueAlmoxarifado == null) {
				habilitaBotao = Boolean.FALSE;
				habilitaBotaoGravar = Boolean.TRUE;
				apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ERRO_ADD_ITENS");
				
			}else{

				Boolean novaTransf = Boolean.FALSE;
				if(trfSeq ==null && transferencia == null){
					this.gerarTransferencia();
					novaTransf = Boolean.TRUE;
				}

				SceItemTransferencia itemTransferencia = new SceItemTransferencia();
				itemTransferencia.setTransferencia(getTransferencia());
				itemTransferencia.setQuantidade(getQtdeEnviada());
				itemTransferencia.setQtdeEnviada(getQtdeEnviada());
				if(getEstoqueAlmoxarifado()!=null){
					itemTransferencia.setUnidadeMedida(getEstoqueAlmoxarifado().getUnidadeMedida());
				}
				itemTransferencia.setEstoqueAlmoxarifado(getEstoqueAlmoxarifado());
				itemTransferencia.setEstoqueAlmoxarifadoOrigem(this.estoqueFacade.obterEstoqueAlmoxarifadoOrigem(getTransferencia().getAlmoxarifado().getSeq(), getEstoqueAlmoxarifado().getMaterial().getCodigo(), getEstoqueAlmoxarifado().getFornecedor().getNumero()));

				estoqueFacade.inserir(itemTransferencia);
				this.emEdicao = Boolean.TRUE;
				this.qtdeEnviada =  null;
				this.estoqueAlmoxarifado = null;
				habilitaBotao = Boolean.FALSE;
				habilitaBotaoGravar = Boolean.TRUE;
				this.inicio();

				if(novaTransf){
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_TRANSFERENCIA", transferencia.getSeq());
				}else{
					apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_ADICIONAR_ITEM_TRANSFERENCIA", itemTransferencia.getEstoqueAlmoxarifado().getMaterial().getNome());
				}
			}


		} catch (BaseException e) {

			apresentarExcecaoNegocio(e);
			
			if (this.listaItensTranferencia != null && this.listaItensTranferencia.size() >= 1) {
				habilitaBotao = Boolean.TRUE;
				habilitaBotaoGravar = Boolean.TRUE;
				
			} else {
				habilitaBotao = Boolean.FALSE;
				habilitaBotaoGravar = Boolean.TRUE;
			}
		}
	}


	public List<SceEstoqueAlmoxarifado> obterMaterial(String paramPesq) throws ApplicationBusinessException {

		AghParametros parametroFornecedor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		Integer frnNumero = null;
		Short almOrigemSeq = null;
		Short almDestinoSeq = null;

		if(almoxarifadoOrigem == null || almoxarifadoDestino ==null){
			apresentarMsgNegocio(Severity.WARN,"MENSAGEM_CAMPOS_OBRIGATORIOS_ADD_TRASNF");
			return new ArrayList<SceEstoqueAlmoxarifado>();
		}else if(this.transferencia != null){
			almOrigemSeq = this.transferencia.getAlmoxarifado().getSeq();
			almDestinoSeq = this.transferencia.getAlmoxarifadoRecebimento().getSeq();
		}else{
			almOrigemSeq = almoxarifadoOrigem.getSeq();
			almDestinoSeq = almoxarifadoDestino.getSeq();
		}

		if (parametroFornecedor != null) {

			frnNumero = parametroFornecedor.getVlrNumerico().intValue();

		}

		return this.estoqueFacade.pesquisarMateriaisPorTransferencia(almOrigemSeq, almDestinoSeq, frnNumero, paramPesq);
	}
	
	
	public Long obterMaterialCount(String paramPesq) throws ApplicationBusinessException {

		AghParametros parametroFornecedor = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		Integer frnNumero = null;
		Short almOrigemSeq = null;
		Short almDestinoSeq = null;

		if(almoxarifadoOrigem == null || almoxarifadoDestino ==null){
			return 0l;
		}else if(this.transferencia != null){
			almOrigemSeq = this.transferencia.getAlmoxarifado().getSeq();
			almDestinoSeq = this.transferencia.getAlmoxarifadoRecebimento().getSeq();
		}else{
			almOrigemSeq = almoxarifadoOrigem.getSeq();
			almDestinoSeq = almoxarifadoDestino.getSeq();
		}

		if (parametroFornecedor != null) {
			frnNumero = parametroFornecedor.getVlrNumerico().intValue();
		}

		return this.estoqueFacade.pesquisarMateriaisPorTransferenciaCount(almOrigemSeq, almDestinoSeq, frnNumero, paramPesq);
	}
    
    public String getAssinaturaRequisitante(){
    	if(this.transferencia.getServidor().getUsuario().toString().equalsIgnoreCase(this.obterLoginUsuarioLogado().toString())){
    		return this.transferencia.getServidor().getPessoaFisica().getNomeUsual() != null ?
    			   this.transferencia.getServidor().getPessoaFisica().getNomeUsual():
    			   this.transferencia.getServidor().getPessoaFisica().getNome();
    	}
    	return "";
    }
	// Metodo para pesquisa na suggestion box de Pacote
	public List<ScePacoteMateriais> obterScePacoteMaterial(String objPesquisa) {


		if(almoxarifadoDestino == null){
			apresentarMsgNegocio(Severity.WARN,"MENSAGEM_CAMPOS_OBRIGATORIOS_PACOTE_TRASNF");
			return new ArrayList<ScePacoteMateriais>();

		} else {

			objPesquisa = almoxarifadoDestino.getCentroCusto().getCodigo().toString();
		}
		
		List<ScePacoteMateriais> listaPacoteMateriais = this.estoqueFacade.pesquisarPacoteMateriaisParaTrasnferenciaEventual(objPesquisa);

		return listaPacoteMateriais;

	}

	/**
	 * Gera uma nova transferência eventual
	 * @return
	 */
	public void gerarTransferencia() {
		try {
			this.transferencia = new SceTransferencia();
			this.transferencia.setAlmoxarifado(almoxarifadoOrigem);
			this.transferencia.setAlmoxarifadoRecebimento(almoxarifadoDestino);

			// Persiste transferência eventual
			this.estoqueBeanFacade.gravarTransferenciaEventualMaterial(transferencia);
			trfSeq = transferencia.getSeq();


		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}


	/**
	 * Obtem lista para sugestion box de almoxarifado de "ORIGEM E DESTINO"
	 * @param param
	 * @return
	 */
	public List<SceAlmoxarifado> obterAlmoxarifado(String param){
		return this.estoqueFacade.obterAlmoxarifadoPorSeqDescricao(param);
	}

	public String getGeradoEm() {

		if(this.transferencia!=null && this.transferencia.getDtGeracao()!=null && transferencia.getServidor()!=null){
			return DateUtil.obterDataFormatada(this.transferencia.getDtGeracao(), DateConstants.DATE_PATTERN_DDMMYYYY) + " - " + transferencia.getServidor().getPessoaFisica().getNome();
		}else{
			return "";
		}

	}

	public String getEfetivadoEm() {

		if(this.transferencia!=null && transferencia.getDtEfetivacao()!=null && transferencia.getServidorEfetivado()!=null){
			return DateUtil.obterDataFormatada(this.transferencia.getDtEfetivacao(), DateConstants.DATE_PATTERN_DDMMYYYY) + " - " + transferencia.getServidorEfetivado().getPessoaFisica().getNome();
		}else{
			return null;

		}
	}
	
	public String visualizarSaldoEstoque(){
		return PESQUISAR_ESTOQUE_ALMOXARIFADO;
	}
	
	public String desbloquearMaterialSemProblema(){
		return MATERIAL_BLOQUEIO_DESBLOQUEIO;
	}
	
	public String desbloquearMaterialComProblema(){
		return PESQUISAR_MATERIAL_BLOQUEIO_DESBLOQUEIO_PROBLEMA;
	}


	public Integer getTrfSeq() {
		return trfSeq;
	}

	public void setTrfSeq(Integer trfSeq) {
		this.trfSeq = trfSeq;
	}

	public Integer getEalSeq() {
		return ealSeq;
	}

	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}


	public SceTransferencia getTransferencia() {
		return transferencia;
	}

	public void setTransferencia(SceTransferencia transferencia) {
		this.transferencia = transferencia;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Integer getQtdeEnviada() {
		return qtdeEnviada;
	}

	public void setQtdeEnviada(Integer qtdeEnviada) {
		this.qtdeEnviada = qtdeEnviada;
	}

	public boolean isHabilitaBotao() {
		return habilitaBotao;
	}

	public void setHabilitaBotao(boolean habilitaBotao) {
		this.habilitaBotao = habilitaBotao;
	}

	public void habilitaBotaoGravar() {
		setHabilitaBotao(true);
	}

	public void desabilitaBotaoGravar() {
		setHabilitaBotao(false);
	}

	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return estoqueAlmoxarifado;
	}

	public void setEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}



	public List<SceItemTransferencia> getListaItensTranferencia() {
		return listaItensTranferencia;
	}

	public void setListaItensTranferencia(
			List<SceItemTransferencia> listaItensTranferencia) {
		this.listaItensTranferencia = listaItensTranferencia;
	}

	public SceItemTransferencia getItemTransferencia() {
		return itemTransferencia;
	}

	public void setItemTransferencia(SceItemTransferencia itemTransferencia) {
		this.itemTransferencia = itemTransferencia;
	}

	public Boolean getHabilitaBotao() {
		return habilitaBotao;
	}

	public void setHabilitaBotao(Boolean habilitaBotao) {
		this.habilitaBotao = habilitaBotao;
	}


	public Boolean getHabilitaBotaoGravar() {
		return habilitaBotaoGravar;
	}

	public void setHabilitaBotaoGravar(Boolean habilitaBotaoGravar) {
		this.habilitaBotaoGravar = habilitaBotaoGravar;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public Date getDtEfetivacao() {
		return dtEfetivacao;
	}

	public void setDtEfetivacao(Date dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}

	public void setAlmoxarifadoOrigem(SceAlmoxarifado almoxarifadoOrigem) {
		this.almoxarifadoOrigem = almoxarifadoOrigem;
	}

	public void setAlmoxarifadoDestino(SceAlmoxarifado almoxarifadoDestino) {
		this.almoxarifadoDestino = almoxarifadoDestino;
	}

	public SceAlmoxarifado getAlmoxarifadoOrigem() {
		return almoxarifadoOrigem;
	}

	public SceAlmoxarifado getAlmoxarifadoDestino() {
		return almoxarifadoDestino;
	}

	public ScePacoteMateriais getPacoteMaterial() {
		return pacoteMaterial;
	}

	public void setPacoteMaterial(ScePacoteMateriais pacoteMaterial) {
		this.pacoteMaterial = pacoteMaterial;
	}

	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
	}

	public Boolean getEfetivado() {
		return efetivado;
	}

	public void setEfetivado(Boolean efetivado) {
		this.efetivado = efetivado;
	}

	public Boolean getEstornado() {
		return estornado;
	}

	public void setEstornado(Boolean estornado) {
		this.estornado = estornado;
	}

	public Boolean getBloquearMaterial() {
		return bloquearMaterial;
	}

	public void setBloquearMaterial(Boolean bloquearMaterial) {
		this.bloquearMaterial = bloquearMaterial;
	}

	public Boolean getHabilitaBotaoAlterar() {
		return habilitaBotaoAlterar;
	}

	public void setHabilitaBotaoAlterar(Boolean habilitaBotaoAlterar) {
		this.habilitaBotaoAlterar = habilitaBotaoAlterar;
	}


	public SceItemTransferenciaId getId() {
		return id;
	}


	public void setId(SceItemTransferenciaId id) {
		this.id = id;
	}



}