package br.gov.mec.aghu.estoque.cadastrosapoio.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacaoSuprimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.vo.ScoServicoCriteriaVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FsoGrupoNaturezaDespesa;
import br.gov.mec.aghu.model.FsoNaturezaDespesa;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.model.ScoSiasgServico;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pela manutenção do cadastro de um serviço.
 * 
 * @author mlcruz
 */

public class ServicoController extends ActionController {

	private static final String ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA = "suprimentos-anexarDocumentoSolicitacaoCompra";
	private static final String SERVICO_LIST = "estoque-servicoList";
	private static final Log LOG = LogFactory.getLog(ServicoController.class);
	private static final long serialVersionUID = 2649782118023878821L;
	
	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private ScoServico servico;
	private Boolean isUpdate;
	private Boolean isReadonly;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private DominioOrigemSolicitacaoSuprimento origem = DominioOrigemSolicitacaoSuprimento.CS;
	private FsoGrupoNaturezaDespesa grupoNaturezaDespesa;

	private Boolean indCatSer;
	
	private ScoServicoCriteriaVO servicoVO;
	
	@PostConstruct
	protected void inicializar(){
		LOG.debug("Iniciando conversation");
		this.begin(conversation);
	}
	
	public void iniciar(){
	 

		
		if(this.servico == null){
			servico = new ScoServico();
			servico.setIndContrato(false);
			servico.setSituacao(DominioSituacao.A);
		}
		
		if (this.grupoNaturezaDespesa == null && isUpdate && servico.getNaturezaDespesa() != null){
    	   grupoNaturezaDespesa = servico.getNaturezaDespesa().getGrupoNaturezaDespesa();
        } 
		else{
			this.grupoNaturezaDespesa = null;
		}

		if (isUpdate || isReadonly) {
			servicoVO = new ScoServicoCriteriaVO();
			servicoVO.setCodigo(servico.getCodigo());
			servicoVO.setNome(servico.getNome());
			servicoVO.setSituacao(servico.getSituacao());
			servicoVO.setGrupo(servico.getGrupoServico());
			servicoVO.setContrato(this.getIndContrato());
			servicoVO.setDescricao(servico.getDescricao());
			servicoVO.setObservacao(servico.getObservacao());
			servicoVO.setGrupoNatureza(servico.getNaturezaDespesa() != null ? servico.getNaturezaDespesa().getGrupoNaturezaDespesa() : null);
			servicoVO.setNatureza(servico.getNaturezaDespesa());

			if (servico.getCatser() != null) {
				List<ScoSiasgServico> catSer = cadastrosBasicosOrcamentoFacade
						.pesquisarCatSer(servico.getCatser());
				servicoVO.setCatSer(catSer.get(0));
			}

			servicoVO.setDtDigitacao(servico.getDtDigitacao());
			servicoVO.setServidor(servico.getServidor());
			servicoVO.setDtAlteracao(servico.getDtAlteracao());
			servicoVO.setDtDesativacao(servico.getDtDesativacao());
			servicoVO.setServidorDesativado(servico.getServidorDesativado());

		} else {
			servicoVO = new ScoServicoCriteriaVO();
			servicoVO.setSituacao(DominioSituacao.A);
			this.setIndContrato(DominioSimNao.N);
		}

		try {
			AghParametros paramCatSer = this.parametroFacade
					.buscarAghParametro(AghuParametrosEnum.P_AGHU_HABILITA_CATSER);

			if (paramCatSer.getVlrTexto().equalsIgnoreCase("S")) {
				this.setIndCatSer(true);
			} else {
				this.setIndCatSer(false);
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}
	
	/**
	 * Obtem grupos de serviço conforme filtro.
	 * 
	 * @param filter Filtro (ID ou parte da descrição do grupo).
	 * @return Grupos de serviço encontrados.
	 */
	public List<ScoGrupoServico> pesquisarGrupos(String filter) {
		return comprasFacade.listarGrupoServico(filter);
	}
		
	/**
	 * Obtem grupos de natureza de despesa.
	 * 
	 * @param filter
	 *            Filtro (código ou descrição do grupo).
	 * @return Grupos.
	 */
	public List<FsoGrupoNaturezaDespesa> pesquisarGruposNaturezaDespesa(
			String filter) {
		return cadastrosBasicosOrcamentoFacade
				.pesquisarGrupoNaturezaDespesaPorCodigoEDescricao(filter);
	}
	
	/**
	 * Obtem naturezas de despesa pelo grupo já selecionado.
	 * 
	 * @param filter
	 *            Código ou descrição da natureza.
	 * @return Naturezas de despesa.
	 */
	public List<FsoNaturezaDespesa> pesquisarNaturezasDespesa(String filter) {
		return cadastrosBasicosOrcamentoFacade.pesquisarNaturezasDespesa(this.servicoVO.getGrupoNatureza(), filter);
	}
	
	public void apagarNaturezaDespesa(){
		servico.setNaturezaDespesa(null);
        servicoVO.setNatureza(null);
	}
	
	
	/**
	 * Grava serviço.
	 * 
	 * @return
	 */
	public void salvar() {

        getDataFromVo();

		formatar(servico);
		
		if (isUpdate) {

			comprasFacade.alterar(servico);

			this.apresentarMsgNegocio(Severity.INFO,
					"MESSAGE_SERVICO_ALTERADO_SUCESSO",
					servico.getCodigoENome());
		} else {
			comprasFacade.incluir(servico);
			isUpdate = true;

			this.apresentarMsgNegocio(Severity.INFO,
							"MESSAGE_SERVICO_GRAVADO_SUCESSO",
							servico.getCodigoENome());
		}
	}


    public void getDataFromVo() {
        FsoNaturezaDespesa natureza =this.servicoVO.getNatureza();
        if(natureza != null) {
            natureza.setGrupoNaturezaDespesa(this.servicoVO.getGrupoNatureza());
            servico.setNaturezaDespesa(natureza);
        }
        if(this.servicoVO.getCatSer() != null){
            servico.setCatser(this.servicoVO.getCatSer().getItCoServico());
        } else {
            servico.setCatser(null);
        }
    }
	
	/**
	 * Formata serviço.
	 * 
	 * @param servico Serviço.
	 */
	private void formatar(ScoServico servico) {
		servico.setNome(StringUtils.trimToNull(
				servico.getNome().toUpperCase()));

		servico.setDescricao(StringUtils.trimToNull(servico.getDescricao()));

		if (servico.getObservacao() != null) {
			servico.setObservacao(StringUtils.trimToNull(
					servico.getObservacao()));
		}

		Date now = new Date();
		RapServidores servidor = servidorLogadoFacade.obterServidorLogado();
		servico.setServidor(servidor);

		if (isUpdate) {
			servico.setDtAlteracao(now);
		} else {
			servico.setDtDigitacao(now);
		}

		if (DominioSituacao.I.equals(servico.getSituacao())) {
			servico.setDtDesativacao(now);
			servico.setServidorDesativado(servidor);
		} else {
			servico.setDtDesativacao(null);
			servico.setServidorDesativado(null);
		}
	}
	
	public String obterPessoaFisicaNome(RapServidores servidor) throws ApplicationBusinessException{
		
		if (servidor != null && servidor.getPessoaFisica() != null){		    
			RapPessoasFisicas pesFis = registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo());				
			return (pesFis != null ? pesFis.getNome(): null);
		}
		else {
			return null;
		}
		
	}
	

	public String anexar(){
		return ANEXAR_DOCUMENTO_SOLICITACAO_COMPRA;
	}
	
	/**
	 * Cancela operação.
	 * 
	 * @return
	 */
	public String cancelar() {
		return SERVICO_LIST;
	}

	public List<ScoSiasgServico> pesquisarCatSer(Object objCatSer){
		return cadastrosBasicosOrcamentoFacade.pesquisarCatSer(objCatSer);
	}

	// Getters/Setters

	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	public Boolean getIsUpdate() {
		return isUpdate;
	}

	public void setIsUpdate(Boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	public Boolean getIsReadonly() {
		return isReadonly;
	}

	public void setIsReadonly(Boolean isReadonly) {
		this.isReadonly = isReadonly;
	}

	public DominioSimNao getIndContrato() {
		return servico != null && servico.getIndContrato() != null ? DominioSimNao
				.getInstance(servico.getIndContrato()) : null;
	}

	public void setIndContrato(DominioSimNao indContrato) {
		servico.setIndContrato(indContrato == null ? null : indContrato.isSim());
	}

	public DominioOrigemSolicitacaoSuprimento getOrigem() {
		return origem;
	}
	



	public Boolean getIndCatSer() {
		return indCatSer;
	}

	public void setIndCatSer(Boolean indCatSer) {
		this.indCatSer = indCatSer;
	}

	public ScoServicoCriteriaVO getServicoVO() {
		return servicoVO;
	}

	public void setServicoVO(ScoServicoCriteriaVO servicoVO) {
		this.servicoVO = servicoVO;
	}
}