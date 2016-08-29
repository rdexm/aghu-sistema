package br.gov.mec.aghu.sig.custos.action;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.AutorizacaoFornecimentoVO;
import br.gov.mec.aghu.sig.custos.business.ICustosSigCadastrosBasicosFacade;
import br.gov.mec.aghu.sig.custos.business.ICustosSigFacade;
import br.gov.mec.aghu.view.VSigAfsContratosServicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterServicosAtividadeController extends ActionController {

	private static final long serialVersionUID = 8942191767824612841L;

	@EJB
	private ICustosSigFacade custosSigFacade;

	@EJB
	private ICustosSigCadastrosBasicosFacade custosSigCadastrosBasicosFacade;

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	@Inject
	private ManterAtividadesController manterAtividadesController;
	
	@EJB
	private IComprasFacade comprasFacade;

	private SigAtividadeServicos servico;

	private List<SigAtividadeServicos> listaServicos;
	private List<SigAtividadeServicos> listaServicosExcluir;

	private boolean edicao;
	private Integer indexOfObjEdicao;
	private Integer codigoServicoExclusao;
	private Integer codigoContratoManual;
	private Integer codigoItemContratoManual;
	private Integer codigoContratoAutomatico;
	private Integer codigoItemContratoAutomatico;
	private boolean contratoAutomatico;
	private boolean possuiAlteracao;
	private SigAtividades atividade;
	private ScoContrato contrato;
	private final Integer ABA_4 = 3;
	private VSigAfsContratosServicos af;
	private SigDirecionadores direcionadorOld;
	private DominioSituacao situacaoOld;
	private Boolean comContrato;
	private AutorizacaoFornecimentoVO voAutForn;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciarAbaServicos(Integer seqAtividade) {
		this.setServico(new SigAtividadeServicos());
		this.setListaServicos(new ArrayList<SigAtividadeServicos>());
		this.setListaServicosExcluir(new ArrayList<SigAtividadeServicos>());
		this.setContrato(null);
		this.setComContrato(true);
		this.setEdicao(false);
		this.setPossuiAlteracao(false);
		this.setContratoAutomatico(true);
		this.setVoAutForn(null);
		if (seqAtividade != null) {
			this.atividade = this.custosSigFacade.obterAtividade(seqAtividade);
			List<SigAtividadeServicos> listResult = custosSigFacade.pesquisarServicosPorSeqAtividade(seqAtividade);
			if (listResult != null && !listResult.isEmpty()) {
				for (SigAtividadeServicos sigAtividadeServicos : listResult) {
					sigAtividadeServicos.setEmEdicao(Boolean.FALSE);
				}
				this.setListaServicos(listResult);
			}
			
			for (Object[] afServico : this.custosSigFacade.pesquisarAutorizFornecServico(seqAtividade)){
				
				if(afServico[0] != null &&
				   StringUtils.isNotBlank(afServico[0].toString())){
					SigAtividadeServicos sigAtividadeServicos = this.custosSigFacade.obterAtividadeServicoDetalhada(Integer.parseInt(afServico[0].toString()));
					
					if(sigAtividadeServicos != null &&
					   afServico[10] != null &&
					   StringUtils.isNotBlank(afServico[10].toString())){
						
						sigAtividadeServicos.setEmEdicao(Boolean.FALSE);
						sigAtividadeServicos.setValorTotalItem(Double.parseDouble(afServico[10].toString()));
						
						if(afServico[7] != null &&
						   StringUtils.isNotBlank(afServico[7].toString())){
							Integer prazoMes = Integer.parseInt(afServico[7].toString());
							
							if(prazoMes < 1){
								sigAtividadeServicos.setEstimadoMes(sigAtividadeServicos.getValorTotalItem());
							}else{
								sigAtividadeServicos.setEstimadoMes(sigAtividadeServicos.getValorTotalItem() / prazoMes);
							}
						}
						
						listResult.add(sigAtividadeServicos);
					}
				}
			}
			this.setListaServicos(listResult);
		}
	}

	public List<SigDirecionadores> getListaDirecionadores() {
		List<SigDirecionadores> listaDirecionadores = new ArrayList<SigDirecionadores>();
		listaDirecionadores = custosSigCadastrosBasicosFacade.pesquisarDirecionadoresAtivosInativo(Boolean.TRUE, Boolean.TRUE);
		return listaDirecionadores;
	}

	public List<ScoItensContrato> getListaItensContrato() {
		List<ScoItensContrato> listaItensContrato = new ArrayList<ScoItensContrato>();
		if (contrato != null) {
			listaItensContrato = contrato.getItensContrato();
		}
		return listaItensContrato;
	}

	public List<SelectItem> listarAfContrato() throws BaseException {
		
		List<VSigAfsContratosServicos> afContratos = new ArrayList<VSigAfsContratosServicos>();
		
		if (contrato != null) {
			afContratos = custosSigFacade.obterAfPorContrato(contrato);
		}
		
		List<SelectItem> itens = new ArrayList<SelectItem>(afContratos.size());
		for (VSigAfsContratosServicos vSigAfsContratosServicos : afContratos) {
			itens.add(new SelectItem(vSigAfsContratosServicos.getSeq(), vSigAfsContratosServicos.getNomeItemAf()));
		}

		this.setCodigoItemContratoAutomatico(null);
		if (this.getAf() != null) {
			this.setCodigoItemContratoAutomatico(this.getAf().getSeq());
		}

		return itens;
	}

	public List<ScoContrato> pesquisarContratoServico(String paramPesquisa) throws BaseException {
		List<ScoContrato> listaResultado = siconFacade.listarContratoByNroOuDescricao(paramPesquisa);
		return listaResultado;
	}

	public List<SelectItem> listarItensContratoServico() throws BaseException {

		List<ScoItensContrato> listaItens = new ArrayList<ScoItensContrato>();
		
		if (contrato != null) {
			listaItens = contrato.getItensContrato();
		}
		
		List<SelectItem> itensSelectOneMenu = null;
		if (listaItens != null) {
			itensSelectOneMenu = new ArrayList<SelectItem>(listaItens.size());
			for (ScoItensContrato itemContrato : listaItens) {
				itensSelectOneMenu.add(new SelectItem(itemContrato.getSeq(), itemContrato.getNomeItemContrato()));
			}
		}

		this.setCodigoItemContratoManual(null);
		if (this.servico.getScoItensContrato() != null) {
			this.setCodigoItemContratoManual(this.servico.getScoItensContrato().getSeq());
		}

		return itensSelectOneMenu;
	}

	public List<ScoItensContrato> pesquisarItensContratoServico(Object paramPesquisa) throws BaseException {
		List<ScoItensContrato> lista = new ArrayList<ScoItensContrato>();
		String srtPesquisa = (String) paramPesquisa;
		if (StringUtils.isNotBlank(srtPesquisa)) {
			for (ScoItensContrato scoItensContrato : contrato.getItensContrato()) {
				if (CoreUtil.isNumeroInteger(paramPesquisa)) {
					if (scoItensContrato.getSeq().intValue() == Integer.valueOf(srtPesquisa)) {
						lista.add(scoItensContrato);
					}
				} else {
					if (scoItensContrato.getNomeItemContrato().toUpperCase().indexOf((srtPesquisa.toUpperCase())) >= 0) {
						lista.add(scoItensContrato);
					}
				}
			}
		} else {
			lista = contrato.getItensContrato();
		}
		return lista;
	}

	public void posSelectionItemContratoManual() throws BaseException {				
		this.servico.setScoItensContrato(siconFacade.getItemContratoBySeq(this.getCodigoItemContratoManual()));
	}

	public void posSelectionItemContratoAutomatico() throws BaseException {

		this.af = null;
		
		List<VSigAfsContratosServicos> afContratos = new ArrayList<VSigAfsContratosServicos>();
		
		if (contrato != null) {
			afContratos = custosSigFacade.obterAfPorContrato(contrato);
		}
		
		for (VSigAfsContratosServicos vSigAfsContratosServicos : afContratos) {
			if (vSigAfsContratosServicos.getSeq().equals(this.getCodigoItemContratoAutomatico())) {
				this.af = vSigAfsContratosServicos;
			}
		}

		if (af != null) {
			ScoAfContrato afSelecionada = this.siconFacade.obterAfContratosById(af.getSeq());
			servico.setScoAfContrato(afSelecionada);
		}
	}

	public void posSelectionContratoServico() {
		if (contrato != null) {
			contratoAutomatico = (DominioOrigemContrato.A == contrato.getIndOrigem());
			if (contratoAutomatico) {
				contrato = siconFacade.obterContratoPorNumeroContrato(contrato.getNrContrato());
			} else {
				contrato.setItensContrato(siconFacade.getItensContratoByContratoManual(contrato));
			}
		}
	}

	public void posDeleteContratoServico() {
		this.setContrato(null);
		if (servico != null) {
			servico.setScoAfContrato(null);
			servico.setScoItensContrato(null);
		}
	}

	public void editarServico(SigAtividadeServicos servico, Integer indServico) {
		setPossuiAlteracao(true);
		this.setEdicao(true);
		this.setIndexOfObjEdicao(indServico);
		this.setServico(servico);
		situacaoOld = servico.getIndSituacao();
		direcionadorOld = servico.getSigDirecionadores();
		this.setComContrato(true);
		
		if (servico.getScoAfContrato() != null) {
			this.setContrato(servico.getScoAfContrato().getScoContrato());
			this.setAf(custosSigFacade.obterAfPorId(servico.getScoAfContrato().getSeq()));
			contratoAutomatico = true;
		} else {
			if(servico.getScoItensContrato() != null &&
			   servico.getScoItensContrato().getContrato() != null){
				this.setContrato(servico.getScoItensContrato().getContrato());
				contratoAutomatico = false;
			}else{
				if(servico.getAutorizacaoForn() != null){
					this.setContrato(null);
					this.setComContrato(false);
					
					this.voAutForn = new AutorizacaoFornecimentoVO();
					
					voAutForn.setNumeroAf(servico.getAutorizacaoForn().getPropostaFornecedor().getLicitacao().getNumero());
					voAutForn.setComplementoAf(servico.getAutorizacaoForn().getNroComplemento());
					voAutForn.setNumeroInternoAf(servico.getAutorizacaoForn().getNumero());
					voAutForn.setNomeServ(servico.getServico().getNome());
					
					contratoAutomatico = false;
				}
			}
		}
		this.getListaServicos().get(this.getIndexOfObjEdicao()).setEmEdicao(Boolean.TRUE);
	}

	public void visualizarServico(SigAtividadeServicos servico) {
		this.setServico(servico);
	}

	public void excluirServico() {
		setPossuiAlteracao(true);
		for (int i = 0; i < this.getListaServicos().size(); i++) {
			SigAtividadeServicos servicoExcluido = this.getListaServicos().get(i);
			if (getCodigoServicoExclusao() == null) {
				if (servicoExcluido.getScoItensContrato() != null && getCodigoContratoManual() != null && getCodigoItemContratoManual() != null) {
					if (servicoExcluido.getScoItensContrato().getNrItem().equals(getCodigoItemContratoManual())
							&& servicoExcluido.getScoItensContrato().getContrato().getSeq().equals(getCodigoContratoManual())) {
						this.getListaServicos().remove(i);
						break;
					}
				}
				if (servicoExcluido.getScoAfContrato() != null && getCodigoContratoAutomatico() != null && getCodigoItemContratoAutomatico() != null) {
					if (servicoExcluido.getScoAfContrato().getSeq().equals(getCodigoItemContratoAutomatico())
							&& servicoExcluido.getScoAfContrato().getScoContrato().getSeq().equals(getCodigoContratoAutomatico())) {
						this.getListaServicos().remove(i);
						break;
					}
				}
			} else {
				if (servicoExcluido.getSeq().equals(getCodigoServicoExclusao())) {
					if (servicoExcluido.getSeq() != null) {
						if (atividade != null && custosSigFacade.verificaAtividadeEstaVinculadaAoObjetoCusto(atividade)) {
							this.setCodigoContratoAutomatico(null);
							this.setCodigoContratoManual(null);
							this.setCodigoItemContratoManual(null);
							this.setCodigoItemContratoAutomatico(null);
							this.setCodigoServicoExclusao(null);
							this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_EXCLUSAO_SERVICO_ATIVIDADE_OBJETO_CUSTO");
							getManterAtividadesController().setTabSelecionada(ABA_4);
							return;
						}
						this.getListaServicosExcluir().add(servicoExcluido);
					}
					this.getListaServicos().remove(i);
					break;
				}
			}
		}
		this.setCodigoContratoAutomatico(null);
		this.setCodigoContratoManual(null);
		this.setCodigoItemContratoManual(null);
		this.setCodigoItemContratoAutomatico(null);
		this.setCodigoServicoExclusao(null);
		getManterAtividadesController().setTabSelecionada(ABA_4);
	}

	public void adicionarServico() {
		try {
			this.setPossuiAlteracao(true);
			this.custosSigFacade.validarInclusaoServicoAtividade(this.servico, this.getListaServicos());
			if (this.servico.getSeq() == null) {
				this.servico.setCriadoEm(new Date());
			}
			this.servico.setRapServidores(this.registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
			this.getListaServicos().add(this.servico);
			this.setServico(new SigAtividadeServicos());
			this.setContrato(null);
			this.setAf(null);
			this.setVoAutForn(null);
			this.setComContrato(true);
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		} 
	}

	public void gravarServico() {
		setPossuiAlteracao(true);
		this.setEdicao(false);
		this.servico.setEmEdicao(Boolean.FALSE);
		try {
			this.servico.setRapServidores(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {
			this.servico.setRapServidores(null);
		}
		this.getListaServicos().set(this.getIndexOfObjEdicao(), this.servico);
		this.setServico(new SigAtividadeServicos());
		this.setContrato(null);
		this.setAf(null);
		this.setComContrato(true);
		this.setVoAutForn(null);
		direcionadorOld = null;
		situacaoOld = null;
	}

	public void cancelarEdicaoServico() {
		servico.setSigDirecionadores(direcionadorOld);
		servico.setIndSituacao(situacaoOld);
		this.setEdicao(false);
		this.getListaServicos().get(this.getIndexOfObjEdicao()).setEmEdicao(Boolean.FALSE);
		this.setServico(new SigAtividadeServicos());
		this.setContrato(null);
		this.setAf(null);
		this.setComContrato(true);
		this.setVoAutForn(null);
		direcionadorOld = null;
		situacaoOld = null;
	}

	// Formatação para a tabela de AF

	public BigDecimal pegarValorTotalAF(ScoAfContrato afContrato) {
		BigDecimal decimal = custosSigFacade.obterAfPorId(afContrato.getSeq()).getTotalItem();
		return decimal;
	}

	public String pegarNumeroAF(ScoAfContrato afContrato) {
		return " AF " + custosSigFacade.obterAfPorId(afContrato.getSeq()).getNumeroAf();
	}

	public String pegarNomeAF(ScoAfContrato afContrato) {
		return custosSigFacade.obterAfPorId(afContrato.getSeq()).getNomeItemAf();
	}

	public BigDecimal pegarValorEstimadoMesAF(ScoAfContrato afContrato) {
		BigDecimal retorno = pegarValorTotalAF(afContrato);
		if (afContrato.getScoContrato() != null && afContrato.getScoContrato().getDtInicioVigencia() != null
				&& afContrato.getScoContrato().getDtFimVigencia() != null) {
			int prazoMessesMinimo = Integer.valueOf(1);
			Calendar cInicio = Calendar.getInstance();
			cInicio.setTime(afContrato.getScoContrato().getDtInicioVigencia());
			Calendar cFim = Calendar.getInstance();
			cFim.setTime(afContrato.getScoContrato().getDtFimVigencia());
			int difMes = cFim.get(Calendar.MONTH) - cInicio.get(Calendar.MONTH);
			int difAno = ((cFim.get(Calendar.YEAR) - cInicio.get(Calendar.YEAR)) * 12);
			int prazoMesses = difAno + difMes;
			if (prazoMesses >= prazoMessesMinimo) {
				retorno = retorno.divide(new BigDecimal(prazoMesses), 2, RoundingMode.HALF_UP);
			}
		}
		return retorno;
	}
	
	public String pegarNomeServicoAF(ScoAfContrato afContrato){
		return custosSigFacade.obterAfPorId(afContrato.getSeq()).getNomeServico();
	}
	
	//Suggestion AF
    public List<AutorizacaoFornecimentoVO> buscarAutorizacaoFornecimento(String paramPesquisa){
		
		List<AutorizacaoFornecimentoVO> listVOAF = new ArrayList<AutorizacaoFornecimentoVO>();
		List<Object[]> result = custosSigFacade.buscarAutorizacaoFornecimento(paramPesquisa);
		
		for(Object[] af : result){
			AutorizacaoFornecimentoVO vo = new AutorizacaoFornecimentoVO(af);
			listVOAF.add(vo);
		}
		
		return listVOAF; 
	}

	
	public void ajustarCombosContratoItem(){
		setComContrato(getComContrato());
	}

	public void posSelectionSuggestionAF(){
		if(this.voAutForn != null){
			this.servico.setAutorizacaoForn(this.autFornecimentoFacade.obterAfByNumeroComPropostaFornecedor(this.voAutForn.getNumeroInternoAf()));
			this.servico.setServico(comprasFacade.obterServicoPorId(this.voAutForn.getCodigoServ()));
			this.servico.setValorTotalItem(this.voAutForn.getTotalItem());
		}
	}


	// gets e sets

	public boolean isEdicao() {
		return edicao;
	}

	public void setEdicao(boolean edicao) {
		this.edicao = edicao;
	}

	public Integer getIndexOfObjEdicao() {
		return indexOfObjEdicao;
	}

	public void setIndexOfObjEdicao(Integer indexOfObjEdicao) {
		this.indexOfObjEdicao = indexOfObjEdicao;
	}

	public void setPossuiAlteracao(boolean possuiAlteracao) {
		this.possuiAlteracao = possuiAlteracao;
	}

	public boolean isPossuiAlteracao() {
		return possuiAlteracao;
	}

	public SigAtividadeServicos getServico() {
		return servico;
	}

	public void setServico(SigAtividadeServicos servico) {
		this.servico = servico;
	}

	public List<SigAtividadeServicos> getListaServicos() {
		return listaServicos;
	}

	public void setListaServicos(List<SigAtividadeServicos> listaServicos) {
		this.listaServicos = listaServicos;
	}

	public List<SigAtividadeServicos> getListaServicosExcluir() {
		return listaServicosExcluir;
	}

	public void setListaServicosExcluir(List<SigAtividadeServicos> listaServicosExcluir) {
		this.listaServicosExcluir = listaServicosExcluir;
	}

	public ScoContrato getContrato() {
		return contrato;
	}

	public void setContrato(ScoContrato contrato) {
		this.contrato = contrato;
	}

	public boolean isContratoAutomatico() {
		return contratoAutomatico;
	}

	public void setContratoAutomatico(boolean contratoAutomatico) {
		this.contratoAutomatico = contratoAutomatico;
	}

	public Integer getCodigoServicoExclusao() {
		return codigoServicoExclusao;
	}

	public void setCodigoServicoExclusao(Integer codigoServicoExclusao) {
		this.codigoServicoExclusao = codigoServicoExclusao;
	}

	public ManterAtividadesController getManterAtividadesController() {
		return manterAtividadesController;
	}

	public void setManterAtividadesController(ManterAtividadesController manterAtividadesController) {
		this.manterAtividadesController = manterAtividadesController;
	}

	public VSigAfsContratosServicos getAf() {
		return af;
	}

	public void setAf(VSigAfsContratosServicos af) {
		this.af = af;
	}

	public Integer getCodigoContratoManual() {
		return codigoContratoManual;
	}

	public void setCodigoContratoManual(Integer codigoContratoManual) {
		this.codigoContratoManual = codigoContratoManual;
	}

	public Integer getCodigoItemContratoManual() {
		return codigoItemContratoManual;
	}

	public void setCodigoItemContratoManual(Integer codigoItemContratoManual) {
		this.codigoItemContratoManual = codigoItemContratoManual;
	}

	public Integer getCodigoContratoAutomatico() {
		return codigoContratoAutomatico;
	}

	public void setCodigoContratoAutomatico(Integer codigoContratoAutomatico) {
		this.codigoContratoAutomatico = codigoContratoAutomatico;
	}

	public Integer getCodigoItemContratoAutomatico() {
		return codigoItemContratoAutomatico;
	}

	public void setCodigoItemContratoAutomatico(Integer codigoItemContratoAutomatico) {
		this.codigoItemContratoAutomatico = codigoItemContratoAutomatico;
	}
	
	public Boolean getComContrato() {
		return comContrato;
	}

	public void setComContrato(Boolean comContrato) {
		this.comContrato = comContrato;
	}

	public AutorizacaoFornecimentoVO getVoAutForn() {
		return voAutForn;
	}

	public void setVoAutForn(AutorizacaoFornecimentoVO voAutForn) {
		this.voAutForn = voAutForn;
	}

}
