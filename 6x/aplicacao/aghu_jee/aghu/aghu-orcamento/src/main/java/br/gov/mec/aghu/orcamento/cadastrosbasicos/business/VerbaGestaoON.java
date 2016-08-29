package br.gov.mec.aghu.orcamento.cadastrosbasicos.business;

import static br.gov.mec.aghu.core.utils.DateUtil.truncaData;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.solicitacaocompra.business.ISolicitacaoComprasFacade;
import br.gov.mec.aghu.compras.solicitacaoservico.business.ISolicitacaoServicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoFontesXVerbaGestao;
import br.gov.mec.aghu.model.FsoParametrosOrcamento;
import br.gov.mec.aghu.model.FsoVerbaGestao;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoSolicitacaoDeCompra;
import br.gov.mec.aghu.model.ScoSolicitacaoServico;
import br.gov.mec.aghu.orcamento.dao.FsoFontesXVerbaGestaoDAO;
import br.gov.mec.aghu.orcamento.dao.FsoParametrosOrcamentoDAO;
import br.gov.mec.aghu.orcamento.dao.FsoVerbaGestaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class VerbaGestaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(VerbaGestaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private FsoFontesXVerbaGestaoDAO fsoFontesXVerbaGestaoDAO;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private ISolicitacaoServicoFacade solicitacaoServicoFacade;
	
	@Inject
	private FsoParametrosOrcamentoDAO fsoParametrosOrcamentoDAO;
	
	@Inject
	private FsoVerbaGestaoDAO fsoVerbaGestaoDAO;
	
	@EJB
	private ISolicitacaoComprasFacade solicitacaoComprasFacade;

	private static final long serialVersionUID = -1757502555867343793L;

	public enum CadastroVerbaGestaoONExceptionCode implements
			BusinessExceptionCode {
		MENSAGEM_JA_EXISTE_DESCRICAO_VERBA_GESTAO, MENSAGEM_FONTE_RECURSO_JA_VINCULADA, MENSAGEM_FONTE_RECURSO_PRIORIDADE, 
		DATA_INICIAL_MAIOR_QUE_FINAL_VERBA_GESTAO, DATA_INICIAL_MAIOR_QUE_FINAL_FONTE_RECURSO, 
		MENSAGEM_CAMPO_OBRIGATORIO_DATA_INICIAL, MENSAGEM_FALTA_FONTE_RECURSO_ATIVA, 
		MENSAGEM_VERBA_GESTAO_ASSOCIADA_SC,MENSAGEM_VERBA_GESTAO_ASSOCIADA_SS,MENSAGEM_CAMPO_OBRIGATORIO_NUMERO_INTERNO,
		MENSAGEM_PERIODO_FONTE_DIFERENTE_VERBA_GESTAO,
		MENSAGEM_ALTERACAO_DESCRICAO_NAO_PERMITIDA,MENSAGEM_VERBA_GESTAO_ASSOCIADA_PARAMETRO_ORCAMENTO,
		MENSAGEM_INATIVACAO_NAO_PERMITIDA_SC, MENSAGEM_INATIVACAO_NAO_PERMITIDA_SS, MENSAGEM_NUMERO_SIAFI_SEIS_DIGITOS;
	}

	public List<FsoFontesXVerbaGestao> pesquisarFontesXVerba(FsoVerbaGestao verbaGestao) {
		return getFsoFontesXVerbaGestaoDAO().pesquisarFontesXVerba(verbaGestao);
	}

	public void gravaFontesRecursoXVerbaGestao(FsoVerbaGestao verbaGestao,
			List<FsoFontesXVerbaGestao> inserirFontesXVerba,
			List<FsoFontesXVerbaGestao> removerFontesXVerba)
			throws ApplicationBusinessException {

		if (verbaGestao != null && verbaGestao.getIndConvEspecial() !=null && verbaGestao.getIndConvEspecial() && verbaGestao.getNroInterno() == null) {
			throw new ApplicationBusinessException (CadastroVerbaGestaoONExceptionCode.MENSAGEM_CAMPO_OBRIGATORIO_NUMERO_INTERNO);
		}
		
		if (verbaGestao != null && verbaGestao.getNroConvSiafi() != null && verbaGestao.getNroConvSiafi().toString().length() != 6) {
			throw new ApplicationBusinessException (CadastroVerbaGestaoONExceptionCode.MENSAGEM_NUMERO_SIAFI_SEIS_DIGITOS);
		}
		
		validarAlteracaoSituacaoPermitida(verbaGestao);
		validarAlteracaoDescricaoPermitida(verbaGestao);
		validaPeriodoVerbaGestao(verbaGestao);
		validaPeriodoFonteRecurso(inserirFontesXVerba);
		validaPeriodoVigenciaFonteRecurso(verbaGestao, inserirFontesXVerba);
		
		// Valida descricao Etnica
		validaDescricaoUnica(verbaGestao);

		// Valida Intersecao do periodo de vigencia das Fontes de Recurso
		validaInterseccaoVigenciaFonteRecurso(verbaGestao, inserirFontesXVerba);

		// Valida prioridade das Fontes de Recurso
		validaPrioridadeFonteRecurso(inserirFontesXVerba);

		// Validacao da Situacao da Verba de Gestao com a situacao da lista de
		// Fontes de Recurso
		validaSituacaoVerbaXSituacaoFontesRecurso(verbaGestao,inserirFontesXVerba);

		// Persiste a Verba de Gestao
		if(verbaGestao.getSeq() == null){
			getFsoVerbaGestaoDAO().persistir(verbaGestao);	
		} else {
			getFsoVerbaGestaoDAO().merge(verbaGestao);
		}
		getFsoVerbaGestaoDAO().flush();

		for (FsoFontesXVerbaGestao fonteXVerba : removerFontesXVerba) {
			if (fonteXVerba.getSeq() != null) {
				getFsoFontesXVerbaGestaoDAO().removerPorId(fonteXVerba.getSeq());
			}
		}

		getFsoFontesXVerbaGestaoDAO().flush();
		
		FsoVerbaGestao verba = getFsoVerbaGestaoDAO().obterPorChavePrimaria(verbaGestao.getSeq());
		if (verba != null) {
			for (FsoFontesXVerbaGestao fonteXVerba : inserirFontesXVerba) {
				if (fonteXVerba.getSeq() != null) {
					fonteXVerba.setVerbaGestao(verba);
					getFsoFontesXVerbaGestaoDAO().atualizar(fonteXVerba);
				} else {
					fonteXVerba.setVerbaGestao(verba);
					getFsoFontesXVerbaGestaoDAO().persistir(fonteXVerba);
				}
			}
		}
		
		getFsoFontesXVerbaGestaoDAO().flush();
	}

	public void validaPeriodoVigenciaFonteRecurso(FsoVerbaGestao verbaGestao,
			List<FsoFontesXVerbaGestao> listaFonteRecursos) throws ApplicationBusinessException {
		
		if (verbaGestao != null && verbaGestao.getIndConvEspecial() != null && verbaGestao.getIndConvEspecial()) {
			if (verbaGestao.getDtIniConv() != null && verbaGestao.getDtFimConv() != null) {
				Date inicioTruncado = DateUtil.truncaData(verbaGestao.getDtIniConv());
				Date fimTruncado = DateUtil.truncaData(verbaGestao.getDtFimConv());
				
				if (listaFonteRecursos != null) {
					for (FsoFontesXVerbaGestao item : listaFonteRecursos) {
						if (!DateUtil.entre(DateUtil.truncaData(item.getDtVigIni()), inicioTruncado, fimTruncado) || (
							item.getDtVigFim() != null && !DateUtil.entre(DateUtil.truncaData(item.getDtVigFim()), inicioTruncado, fimTruncado))) {
							throw new ApplicationBusinessException(
									CadastroVerbaGestaoONExceptionCode.MENSAGEM_PERIODO_FONTE_DIFERENTE_VERBA_GESTAO);
						}
					}
				}
			}
		}
	}
	
	private void validaDescricaoUnica(FsoVerbaGestao verbaGestao)
			throws ApplicationBusinessException {
		if (verbaGestao == null || verbaGestao.getDescricao() == null
				|| verbaGestao.getDescricao().isEmpty()) {
			return;
		}

		List<FsoVerbaGestao> verbas = getFsoVerbaGestaoDAO()
				.pesquisarVerbaGestaoAtivaPorDescricao(verbaGestao);

		if (verbas.size() > 0
				|| (verbas.size() == 1 && verbas.get(0).getSeq() != verbaGestao
						.getSeq())) {
			throw new ApplicationBusinessException(
					CadastroVerbaGestaoONExceptionCode.MENSAGEM_JA_EXISTE_DESCRICAO_VERBA_GESTAO);
		}
	}

	private void validaInterseccaoVigenciaFonteRecurso(
			FsoVerbaGestao verbaGestao,
			List<FsoFontesXVerbaGestao> listaFonteXVerba)
			throws ApplicationBusinessException {
		// Verba Inativa nao precisa testar insersecao de periodos
		if (verbaGestao.getSituacao().equals(DominioSituacao.I)) {
			return;
		}

		// Testa intersecao de datas entre Fontes de Recursos que estao sendo
		// inclusas
		for (FsoFontesXVerbaGestao fonteXVerba : listaFonteXVerba) {
			for (FsoFontesXVerbaGestao fonteXVerbaASeremInclusas : listaFonteXVerba) {
				if ((fonteXVerba.getFonteRecursoFinanceiro().getCodigo() == fonteXVerbaASeremInclusas
						.getFonteRecursoFinanceiro().getCodigo() && fonteXVerba != fonteXVerbaASeremInclusas)) {
					if (existeInterseccao(fonteXVerba, fonteXVerbaASeremInclusas)) {
						throw new ApplicationBusinessException(
								CadastroVerbaGestaoONExceptionCode.MENSAGEM_FONTE_RECURSO_JA_VINCULADA);
					}
				}
			}
		}
	}
	
	/**
	 * Verifica se existe intersecção entre os períodos de duas fontes de
	 * recurso de uma verba de gestão.
	 * 
	 * @param fonteX Fonte X 
	 * @param fonteY Fonte Y
	 * @return Flag
	 */
	private boolean existeInterseccao(FsoFontesXVerbaGestao fonteX, FsoFontesXVerbaGestao fonteY) {
		Date fonteXInicio = truncaData(fonteX.getDtVigIni());
		Date fonteXFim = truncaData(fonteX.getDtVigFim());
		Date fonteYInicio = truncaData(fonteY.getDtVigIni());
		Date fonteYFim = truncaData(fonteY.getDtVigFim());
		
		if (fonteXInicio.equals(fonteYInicio)) {
			return true;
		} else if (fonteXInicio.after(fonteYInicio)) {
			if (fonteYFim == null) {
				return true;
			} else if (!fonteXInicio.after(fonteYFim)) {
				return true;
			}
		}
		
		if (fonteXFim == null) {
			if (fonteYFim == null) {
				return true;
			} else if (!fonteYFim.before(fonteXInicio)) {
				return true;
			}
		} else if (fonteYFim == null) {
			if (!fonteXFim.before(fonteYInicio)) {
				return true;
			}
		} else if (fonteXFim.equals(fonteYFim)) {
			return true;
		} else if (fonteXFim.after(fonteYFim)) {
			return !fonteXInicio.after(fonteYFim);
		} else {
			return !fonteXFim.before(fonteYInicio);
		}
		
		return false;
	}

	private void validaPrioridadeFonteRecurso(
			List<FsoFontesXVerbaGestao> fonteXVerbaASeremInclusas)
			throws ApplicationBusinessException{
		
		Calendar dataReferencia = Calendar.getInstance();
		dataReferencia.add(Calendar.DAY_OF_MONTH, -1);
			
		for (FsoFontesXVerbaGestao listaFonteXVerba : fonteXVerbaASeremInclusas) {
			for (FsoFontesXVerbaGestao fonteXVerba : fonteXVerbaASeremInclusas) {								
				if (fonteXVerba != listaFonteXVerba && 
						fonteXVerba.getIndPrioridade() == listaFonteXVerba.getIndPrioridade() &&
						existeInterseccao(listaFonteXVerba, fonteXVerba)) {
					throw new ApplicationBusinessException(
							CadastroVerbaGestaoONExceptionCode.MENSAGEM_FONTE_RECURSO_PRIORIDADE);
				}
			}
		}
	}

	private void validaPeriodoVerbaGestao(FsoVerbaGestao verbaGestao)
			throws ApplicationBusinessException{

		if (verbaGestao != null && verbaGestao.getIndConvEspecial() != null
				&& verbaGestao.getIndConvEspecial()
				&& verbaGestao.getDtIniConv() == null) {
			throw new ApplicationBusinessException(
					CadastroVerbaGestaoONExceptionCode.MENSAGEM_CAMPO_OBRIGATORIO_DATA_INICIAL);
		}

		if (verbaGestao == null || verbaGestao.getDtIniConv() == null
				|| verbaGestao.getDtFimConv() == null) {
			return;
		}

		if (verbaGestao.getDtIniConv().after(verbaGestao.getDtFimConv())) {
			throw new ApplicationBusinessException(
					CadastroVerbaGestaoONExceptionCode.DATA_INICIAL_MAIOR_QUE_FINAL_VERBA_GESTAO);
		}
	}

	private void validaPeriodoFonteRecurso(
			List<FsoFontesXVerbaGestao> listaFonteXVerba)
			throws ApplicationBusinessException{
		if (listaFonteXVerba == null) {
			return;
		}

		for (FsoFontesXVerbaGestao fonteXVerba : listaFonteXVerba) {
			if (fonteXVerba.getDtVigIni() == null
					|| fonteXVerba.getDtVigFim() == null) {
				break;
			}

			if (fonteXVerba.getDtVigIni().after(fonteXVerba.getDtVigFim())) {
				throw new ApplicationBusinessException(
						CadastroVerbaGestaoONExceptionCode.DATA_INICIAL_MAIOR_QUE_FINAL_FONTE_RECURSO);
			}
		}
	}

	private void validarAlteracaoDescricaoPermitida(FsoVerbaGestao verbaGestao) throws ApplicationBusinessException{
		if (verbaGestao != null) {
			FsoVerbaGestao verbaGestaoOriginal = this.getFsoVerbaGestaoDAO().obterOriginal(verbaGestao);
			if (verbaGestaoOriginal != null && verbaGestaoOriginal.getDescricao() != null && verbaGestao.getDescricao() != null && 
					!verbaGestaoOriginal.getDescricao().equals(verbaGestao.getDescricao())) {
				List<ScoAutorizacaoForn> listaAfs = this.getAutFornecimentoFacade().pesquisarVerbaGestaoAssociadaAf(verbaGestao);
				
				if (listaAfs != null && !listaAfs.isEmpty()) {
					throw new ApplicationBusinessException(
							CadastroVerbaGestaoONExceptionCode.MENSAGEM_ALTERACAO_DESCRICAO_NAO_PERMITIDA, this.obterListaAfsAssociadas(listaAfs));
				}
			}
		}
	}
	
	private void validarAlteracaoSituacaoPermitida(FsoVerbaGestao verbaGestao) throws ApplicationBusinessException {
		if (verbaGestao != null) {
			FsoVerbaGestao verbaGestaoOriginal = this.getFsoVerbaGestaoDAO().obterOriginal(verbaGestao);
			if (verbaGestaoOriginal != null && verbaGestaoOriginal.getSituacao() != null && verbaGestao.getSituacao() != null && 
					verbaGestaoOriginal.getSituacao().equals(DominioSituacao.A) && verbaGestao.getSituacao().equals(DominioSituacao.I)) {
				
				this.validarExistenciaSolicitacaoAssociadaVerbaGestao(verbaGestaoOriginal, true);
			}
		}
	}
	
	private void validaSituacaoVerbaXSituacaoFontesRecurso(FsoVerbaGestao verbaGestao, List<FsoFontesXVerbaGestao> inserirFontesXVerba)
																				throws ApplicationBusinessException{

		// Verba de Gestao Inativa pode ser inclusa/alterada sem validar suas
		// Fontes de Recurso
		if (verbaGestao != null && verbaGestao.getSituacao() != null
				&& verbaGestao.getSituacao().equals(DominioSituacao.I)) {
			return;
		}

		// Na alteracao de verba ativa sem fontes de recurso, as alteracoes sao
		// persistidas alterando a situacao da verba para Inativo
		if (verbaGestao.getSeq() != null) {
			if (verbaGestao.getSituacao().equals(DominioSituacao.A) && inserirFontesXVerba.size() == 0) {
				verbaGestao.setSituacao(DominioSituacao.I);
				return;
			}
		}

		// Para a inclusao de uma Verba ativa, ela deve possuir Fontes de
		// Recurso ativos
		if (verbaGestao.getSeq() == null
				&& (inserirFontesXVerba == null || inserirFontesXVerba.size() == 0)) {
			throw new ApplicationBusinessException(CadastroVerbaGestaoONExceptionCode.MENSAGEM_FALTA_FONTE_RECURSO_ATIVA);
		}

		// Valida a vigencia das Fontes de Recurso
		// Deve existir pelo menos uma fonte de recurso com data final da
		// vigencia maior que o dia anterior
		Calendar dataReferencia = Calendar.getInstance();
		dataReferencia.add(Calendar.DAY_OF_MONTH, -1);

		boolean existeFonteRecursoAtiva = false;

		for (FsoFontesXVerbaGestao fonte : inserirFontesXVerba) {
			if (fonte.getDtVigFim() == null) {
				existeFonteRecursoAtiva = true;
			} else {
				Calendar dataFimVigencia = Calendar.getInstance();
				dataFimVigencia.setTime(fonte.getDtVigFim());

				if (dataReferencia.before(dataFimVigencia)) {
					existeFonteRecursoAtiva = true;
				}
			}
		}

		if (!existeFonteRecursoAtiva) {
			// Uma Verba ativa nao pode ser inclusa sem ao menos uma Fonte de
			// Recurso ativa
			if (verbaGestao.getSeq() == null) {
				throw new ApplicationBusinessException(CadastroVerbaGestaoONExceptionCode.MENSAGEM_FALTA_FONTE_RECURSO_ATIVA);
			} else {
				// Alterando uma Verba ja existente, ela passa para situacao
				// inativa se nao possuir ao menos uma Fonte de Recurso ativa
				verbaGestao.setSituacao(DominioSituacao.I);
			}
		}

	}

	/**
	 * Exclui uma verba de gestão
	 */
	public void excluirVerbaGestao(Integer seq) throws ApplicationBusinessException {
		FsoVerbaGestao verbaGestao = this.getFsoVerbaGestaoDAO().obterPorChavePrimaria(seq);

		if (verbaGestao == null) {
			throw new BaseRuntimeException(ApplicationBusinessExceptionCode.REGISTRO_NULO_EXCLUSAO);
		}

		this.validarExistenciaSolicitacaoAssociadaVerbaGestao(verbaGestao, false);
		
		List<FsoParametrosOrcamento> listaParamOrcament = this.getFsoParametrosOrcamentoDAO().
				pesquisarVerbaGestaoAssociadaParametroOrcamento(verbaGestao);
		
		
		if (listaParamOrcament != null && listaParamOrcament.size() > 0) {
			throw new ApplicationBusinessException(
					CadastroVerbaGestaoONExceptionCode.MENSAGEM_VERBA_GESTAO_ASSOCIADA_PARAMETRO_ORCAMENTO, this.obterListaParametrosOrcamentariosAssociados(listaParamOrcament));
		}
		List<FsoFontesXVerbaGestao> listaFontesVerbas = this.getFsoFontesXVerbaGestaoDAO().pesquisarFontesXVerba(verbaGestao);
		
		if (listaFontesVerbas != null) {
			for (FsoFontesXVerbaGestao fonteVerba : listaFontesVerbas) {
				this.getFsoFontesXVerbaGestaoDAO().remover(fonteVerba);
			}
		}
		
		this.getFsoVerbaGestaoDAO().remover(verbaGestao);
	}
		
	private void validarExistenciaSolicitacaoAssociadaVerbaGestao(FsoVerbaGestao verbaGestao, Boolean filtraEfetivada) throws ApplicationBusinessException {
		List<ScoSolicitacaoDeCompra> listaSolCompras = this.getSolicitacaoComprasFacade().
				buscarSolicitacaoComprasAssociadaVerbaGestao(verbaGestao, filtraEfetivada);
		
		List<ScoSolicitacaoServico> listaSolServico = this.getSolicitacaoServicoFacade().
				buscarSolicitacaoServicoAssociadaVerbaGestao(verbaGestao, filtraEfetivada);
		
		if (listaSolCompras != null && listaSolCompras.size() > 0)	 {
			if (filtraEfetivada) {
				throw new ApplicationBusinessException(
						CadastroVerbaGestaoONExceptionCode.MENSAGEM_INATIVACAO_NAO_PERMITIDA_SC, this.obterListaSolicitacoesComprasAssociadas(listaSolCompras));
			} else {
				throw new ApplicationBusinessException(
						CadastroVerbaGestaoONExceptionCode.MENSAGEM_VERBA_GESTAO_ASSOCIADA_SC, this.obterListaSolicitacoesComprasAssociadas(listaSolCompras));
			}
		}

		if (listaSolServico != null && listaSolServico.size() > 0) {
			if (filtraEfetivada) {
				throw new ApplicationBusinessException(
						CadastroVerbaGestaoONExceptionCode.MENSAGEM_INATIVACAO_NAO_PERMITIDA_SS, this.obterListaSolicitacoesServicoAssociadas(listaSolServico));
			} else {
				throw new ApplicationBusinessException(
						CadastroVerbaGestaoONExceptionCode.MENSAGEM_VERBA_GESTAO_ASSOCIADA_SS, this.obterListaSolicitacoesServicoAssociadas(listaSolServico));	
			}
			
		}
		
	}
	
	private String obterListaSolicitacoesComprasAssociadas(List<ScoSolicitacaoDeCompra> listaSolCompras) {
		StringBuilder msg = new StringBuilder();
		
		for (ScoSolicitacaoDeCompra solicit : listaSolCompras) {
			msg.append(solicit.getNumero()).append(',');	
		}
		
		return msg.toString().substring(0, msg.toString().length()-1);
	}
	
	private String obterListaSolicitacoesServicoAssociadas(List<ScoSolicitacaoServico> listaSolServico) {
		StringBuilder msg = new StringBuilder();
		
		for (ScoSolicitacaoServico solicit : listaSolServico) {
			msg.append(solicit.getNumero()).append(',');	
		}
		
		return msg.toString().substring(0, msg.toString().length()-1);
	}

	private String obterListaAfsAssociadas(List<ScoAutorizacaoForn> listaAfs) {
		StringBuilder msg = new StringBuilder();
		
		for (ScoAutorizacaoForn af : listaAfs) {
			msg.append(af.getPropostaFornecedor().getId().getLctNumero()).append('/').append(af.getNroComplemento()).append(',');	
		}
		
		return msg.toString().substring(0, msg.toString().length()-1);
	}
	
	private String obterListaParametrosOrcamentariosAssociados(List<FsoParametrosOrcamento> listaParams) {
		StringBuilder msg = new StringBuilder();
		
		for (FsoParametrosOrcamento param : listaParams) {
			msg.append(param.getSeq()).append(',');	
		}
		
		return msg.toString().substring(0, msg.toString().length()-1);
	}
	
	protected FsoVerbaGestaoDAO getFsoVerbaGestaoDAO() {
		return fsoVerbaGestaoDAO;
	}

	protected FsoParametrosOrcamentoDAO getFsoParametrosOrcamentoDAO() {
		return fsoParametrosOrcamentoDAO;
	}
	
	protected FsoFontesXVerbaGestaoDAO getFsoFontesXVerbaGestaoDAO() {
		return fsoFontesXVerbaGestaoDAO;
	}

	protected ISolicitacaoComprasFacade getSolicitacaoComprasFacade() {
		return this.solicitacaoComprasFacade;
	}
	
	protected ISolicitacaoServicoFacade getSolicitacaoServicoFacade() {
		return this.solicitacaoServicoFacade;
	}

	protected IAutFornecimentoFacade getAutFornecimentoFacade() {
		return this.autFornecimentoFacade;
	}
}