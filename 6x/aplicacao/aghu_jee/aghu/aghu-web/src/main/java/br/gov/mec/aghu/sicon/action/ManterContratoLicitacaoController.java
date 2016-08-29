package br.gov.mec.aghu.sicon.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoLicitacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

public class ManterContratoLicitacaoController extends ActionController {

	private static final long serialVersionUID = -4302551499297719382L;
	
	private static final String PAGE_MANTER_CONTRATO_AUTOMATICO = "manterContratoAutomatico";
	private static final String PAGE_GERENCIAR_CONTRATOS = "gerenciarContratos";

	private enum ManterContratoLicitacaoControllerExceptionCode implements BusinessExceptionCode {
		LICITACAO_OBRIGATORIO, FORN_OBRIGATORIO, NENHUM_CONTRATO_VINCULADO;
	}

	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;

	private List<ContratoLicitacaoVO> afs;

	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

	private ContratoLicitacaoVO af;

	private ScoLicitacao licitacaoSelected;
	private ScoFornecedor fornecedorSelected;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void init() {
		
		setAf(null);
		setFornecedorSelected(null);
		setLicitacaoSelected(null);

		if (afs == null) {
			afs = new ArrayList<ContratoLicitacaoVO>();
		} else if (afs.size() > 0) {
			//pesquisar();
			afs.clear();
		}
	
	}

	public void pesquisar() {
		try {
			if (licitacaoSelected == null && fornecedorSelected != null) {
				throw new ApplicationBusinessException(ManterContratoLicitacaoControllerExceptionCode.LICITACAO_OBRIGATORIO);
			}

			List<ScoAutorizacaoForn> res = autFornecimentoFacade.listarAfByFornAndLic(licitacaoSelected, fornecedorSelected);

			if (res.size() > 0) {
				updateDataListModel(res);
			} else {
				afs.clear();
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	
	private void updateDataListModel(List<ScoAutorizacaoForn> res) {
		List<ContratoLicitacaoVO> _tempAfsList = new ArrayList<ContratoLicitacaoVO>();
		for (ScoAutorizacaoForn a : res) {
			a.setItensAutorizacaoForn(autFornecimentoFacade.pesquisarItemAfAtivosPorNumeroAf(a.getNumero(), false));
			ContratoLicitacaoVO c = new ContratoLicitacaoVO(a, DominioSimNao.S);
			_tempAfsList.add(c);
		}
		afs = _tempAfsList;

	}

	public String gerarContrato() {

		try {
			if (licitacaoSelected == null) {
				throw new ApplicationBusinessException(ManterContratoLicitacaoControllerExceptionCode.LICITACAO_OBRIGATORIO);
			}
			if (fornecedorSelected == null) {
				throw new ApplicationBusinessException(ManterContratoLicitacaoControllerExceptionCode.FORN_OBRIGATORIO);
			}
			if (!checkSelectOneContr()) {
				throw new ApplicationBusinessException(ManterContratoLicitacaoControllerExceptionCode.NENHUM_CONTRATO_VINCULADO);
			}
			return PAGE_MANTER_CONTRATO_AUTOMATICO;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	private boolean checkSelectOneContr() {
		for (ContratoLicitacaoVO vo : afs) {
			if (vo.getVincularAoContrato() == DominioSimNao.S) {
				return true;
			}
		}
		return false;
	}

	public String getFormatFornText() {
		return formatarNomeCPFCGC(fornecedorSelected);
	}

	public String formatFornTextGrid(ContratoLicitacaoVO vo) {
		return formatarNomeCPFCGC(vo.getAf().getPropostaFornecedor().getFornecedor());
	}

	private String formatarNomeCPFCGC(ScoFornecedor fo) {
		StringBuffer outputStr = new StringBuffer();
		
		if (fo != null ){
			outputStr.append(getCPFCNPJ(fo));
			outputStr.append("  -  ");
		    outputStr.append(fo.getRazaoSocial());
		}
		return outputStr.toString();
	}

	public String getCPFCNPJ(ScoFornecedor fo) {
		
		if (fo != null && fo.getCgc() != null) {
			return CoreUtil.formatarCNPJ(fo.getCgc());
		} else if (fo != null && fo.getCpf() != null) {
			return CoreUtil.formataCPF(fo.getCpf());
		} else {
			return "";
		}
	}

	public String voltar() {

		desatacharAutorizacaoForn();

		return PAGE_GERENCIAR_CONTRATOS;
	}

	public void limpar() {
		desatacharAutorizacaoForn();

		afs.clear();
		setAf(null);
		setFornecedorSelected(null);
		setLicitacaoSelected(null);
	}

	public void desatacharAutorizacaoForn() {
		for (ContratoLicitacaoVO autorizacaoForn : afs) {
			autFornecimentoFacade.desatacharAutorizacaoForn(autorizacaoForn.getAf());
		}
	}

	public List<ScoLicitacao> pesquisarLicitac(String param) {
		try {
			return siconFacade.listarLicitacoesAtivas(param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public void selectFornFromGrid(ContratoLicitacaoVO input) {
		setAf(input);
		setFornecedorSelected(input.getAf().getPropostaFornecedor().getFornecedor());
		pesquisar();
	}

	public List<ScoFornecedor> pesquisarFornecedor(String param) {
		try {
			if (licitacaoSelected == null) {
				throw new ApplicationBusinessException(ManterContratoLicitacaoControllerExceptionCode.LICITACAO_OBRIGATORIO);
			}

			return comprasFacade.listarFornecedoresByLicitacao(param, licitacaoSelected);

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public boolean materialPossuiCodSiasg(ScoMaterial material) {
		return this.cadastrosBasicosSiconFacade.pesquisarMaterialSicon(null, material, DominioSituacao.A, null) != null;
	}

	public boolean servicoPossuiCodSiasg(ScoServico servico) {
		return this.cadastrosBasicosSiconFacade.pesquisarServicoSicon(null, servico, DominioSituacao.A, null) != null;
	}

	public void setupPopup(ContratoLicitacaoVO inp) {
		setAf(inp);
	}

	public ScoLicitacao getLicitacaoSelected() {
		return licitacaoSelected;
	}

	public void setLicitacaoSelected(ScoLicitacao licitacaoSelected) {
		this.licitacaoSelected = licitacaoSelected;
	}

	public ScoFornecedor getFornecedorSelected() {
		return fornecedorSelected;
	}

	public void setFornecedorSelected(ScoFornecedor fornecedorSelected) {
		this.fornecedorSelected = fornecedorSelected;
	}

	public List<ContratoLicitacaoVO> getAfs() {
		return afs;
	}

	public void setAfs(List<ContratoLicitacaoVO> afs) {
		this.afs = afs;
	}

	public ContratoLicitacaoVO getAf() {
		return af;
	}

	public void setAf(ContratoLicitacaoVO af) {
		this.af = af;
	}

}
