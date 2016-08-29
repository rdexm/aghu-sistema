package br.gov.mec.aghu.estoque.pesquisa.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.ItemNotaRecebimentoVO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceNotaRecebimento;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pelo consulta de informações sobre Notas de
 * Recebimento
 */
public class ConsultaNotaRecebimentoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -2286570718056580503L;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IParametroFacade parametroFacade;

	private SceNotaRecebimento notaRecebimento;

	private String origem;

	private List<ItemNotaRecebimentoVO> listaItens = null;

	private boolean comprasAtivo = true;
	
	private Integer seqNotaRecebimento = null;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	public String iniciar() {
	 
        if(seqNotaRecebimento != null){
           this.notaRecebimento = this.estoqueFacade.obterNotaRecebimento(seqNotaRecebimento);	
        }

		if (this.notaRecebimento != null && this.estoqueFacade.obterNotaRecebimento(this.notaRecebimento.getSeq()) == null) {
			apresentarMsgNegocio(Severity.INFO, "REGISTRO_NULO_EXCLUSAO");
			return voltar();
		}

		AghParametros parametro;
		try {
			parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_COMPRAS_ATIVO);
			// O Módulo de Compras do AGHU está INATIVO quando valor numérico do
			// parâmetro for igual a ZERO
			if (parametro != null && BigDecimal.ZERO.equals(parametro.getVlrNumerico())) {
				comprasAtivo = false;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		if (this.notaRecebimento != null) {
			this.setListaItens(estoqueFacade.pesquisarItensNotaRecebimento(this.notaRecebimento.getSeq()));
		}

		return null;

	
	}

	public String voltar() {
		this.comprasAtivo = true;
		this.notaRecebimento = null;
		this.listaItens = new ArrayList<ItemNotaRecebimentoVO>();
		return getOrigem();
	}

	public SceNotaRecebimento getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public List<ItemNotaRecebimentoVO> getListaItens() {
		return listaItens;
	}

	public void setListaItens(List<ItemNotaRecebimentoVO> listaItens) {
		this.listaItens = listaItens;
	}

	public String obterServidorFormatado(RapServidores servidor) throws ApplicationBusinessException {
		StringBuffer str = new StringBuffer();
		if (servidor != null) {
			if (servidor.getPessoaFisica() != null){
				servidor.setPessoaFisica(this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo()));
			}
			
			if (servidor.getId() != null && servidor.getId().getMatricula() != null) {				
				str.append(servidor.getId().getMatricula().toString());				
				if (servidor.getPessoaFisica() != null && servidor.getPessoaFisica().getNome() != null && !"".equals(servidor.getPessoaFisica().getNome())) {
					str.append(" - ").append(servidor.getPessoaFisica().getNome());
				}
			} else if (servidor.getPessoaFisica() != null && servidor.getPessoaFisica().getNome() != null && !"".equals(servidor.getPessoaFisica().getNome())) {
				str.append(servidor.getPessoaFisica().getNome());
			}
		}
		return str.toString();
	}

	public String getNumeroComplementoAF() {
		StringBuffer str = new StringBuffer();
		if (getNotaRecebimento() != null && getNotaRecebimento().getAutorizacaoFornecimento() != null) {
			if (getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor() != null && getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getId() != null) {
				str.append(getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getId().getLctNumero());
				if (getNotaRecebimento().getAutorizacaoFornecimento().getNroComplemento() != null) {
					str.append('/').append(getNotaRecebimento().getAutorizacaoFornecimento().getNroComplemento());
				}
			} else {
				str.append(getNotaRecebimento().getAutorizacaoFornecimento().getNroComplemento());
			}
		}
		return str.toString();
	}

	public String getFornecedorFormatado() {
		StringBuffer str = new StringBuffer();
		ScoFornecedor fornecedor = null;
		if (getNotaRecebimento() != null && getNotaRecebimento().getAutorizacaoFornecimento() != null && getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor() != null
				&& getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor() != null) {
			fornecedor = getNotaRecebimento().getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor();
			if (fornecedor.getNumero() != null) {
				str.append(fornecedor.getNumero());
			}
			if (fornecedor.getRazaoSocial() != null) {
				str.append(" - ");
				str.append(fornecedor.getRazaoSocial());
			}
			if (fornecedor.getNomeFantasia() != null) {
				str.append(" -- ");
				str.append(fornecedor.getNomeFantasia());
			}
		}
		return str.toString();
	}

	public String abreviar(String str, int maxWidth) {
		String abreviado = null;
		if (str != null) {
			abreviado = StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}

	public Double obterValorUnitarioLiquido(ItemNotaRecebimentoVO vo) {
		if (vo != null) {
			return this.estoqueFacade.getValorUnitarioLiquido(vo.getValorUnitarioItemAutorizacaoFornecimento(), vo.getQuantidadeRecebida(), vo.getPercAcrescimo(), vo.getPercAcrescimoItem(),
					vo.getPercDesconto(), vo.getPercDescontoItem(), vo.getPercIpi());
		} else {
			return null;
		}
	}

	public Boolean isEmpty(String str) {
		return StringUtils.isEmpty(str);
	}

	public boolean isComprasAtivo() {
		return comprasAtivo;
	}

	public void setComprasAtivo(boolean comprasAtivo) {
		this.comprasAtivo = comprasAtivo;
	}

	public Integer getSeqNotaRecebimento() {
		return seqNotaRecebimento;
	}

	public void setSeqNotaRecebimento(Integer seqNotaRecebimento) {
		this.seqNotaRecebimento = seqNotaRecebimento;
	}

}
