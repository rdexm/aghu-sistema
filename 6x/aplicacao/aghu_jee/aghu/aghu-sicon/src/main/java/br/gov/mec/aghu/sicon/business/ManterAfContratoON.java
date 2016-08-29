package br.gov.mec.aghu.sicon.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoAfContratoJn;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sicon.dao.ScoAfContratoJnDAO;
import br.gov.mec.aghu.sicon.dao.ScoAfContratosDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.vo.AutorizacaoFornecimentoVO;
import br.gov.mec.aghu.sicon.vo.ItemAutorizacaoFornVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class ManterAfContratoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterAfContratoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ScoAfContratoJnDAO scoAfContratoJnDAO;
	
	@Inject
	private ScoContratoDAO scoContratoDAO;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@Inject
	private ScoAfContratosDAO scoAfContratosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 4105698899651197727L;

	
	public enum ManterAfContratoONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_NECESSARIO_EXISTIR_AF_VINCULADA;
	}

	public void gravar(
			List<AutorizacaoFornecimentoVO> listaAutorizacaoFornecimentoVO,
			ScoContrato contrato) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAfContratosDAO scoAfContratosDAO = this.getScoAfContratosDAO();
		IAutFornecimentoFacade autFornecimentoFacade = this.getAutFornecimentoFacade();
		
		
		validaExistenciaAfVinculada(listaAutorizacaoFornecimentoVO);

		for (AutorizacaoFornecimentoVO aFVO : listaAutorizacaoFornecimentoVO) {
			// AF ainda não vinculada mas com situação Vincular ao Contrato = SIM
			// deve-se  realizar a relação
			if (!aFVO.isJaVinculado()
					&& aFVO.getVincularAoContrato().equals(DominioSimNao.S)) {
				ScoAfContrato scoAfContrato = new ScoAfContrato();
				scoAfContrato.setScoContrato(contrato);
				scoAfContrato.setScoAutorizacoesForn(autFornecimentoFacade
						.obterAfByNumero(aFVO.getAf().getNumero()));

				scoAfContrato.setCriadoEm(new Date());
				scoAfContrato.setServidor(servidorLogado);
				scoAfContrato.setVersion(1);

				scoAfContratosDAO.persistir(scoAfContrato);
				scoAfContratosDAO.flush();

				registrarAfContratoJn(scoAfContrato, DominioOperacoesJournal.INS);

				contrato.setValorTotal(contrato.getValorTotal().add(
						aFVO.getValorProposta()));				
				if (contrato.getSituacao().equals(DominioSituacaoEnvioContrato.E)){
					contrato.setSituacao(DominioSituacaoEnvioContrato.AR);
				}		
				
				ScoContratoDAO scoContratoDAO = this.getScoContratoDAO();
				scoContratoDAO.atualizar(contrato);
				scoContratoDAO.flush();
				
				ScoAutorizacaoForn autorizacaoForn = aFVO.getAf();
				autorizacaoForn.setNroContrato(contrato.getNrContrato().intValue());
				autorizacaoForn.setDtVenctoContrato(contrato.getDtFimVigencia());
				autFornecimentoFacade.atualizarAutorizacaoForn(autorizacaoForn, true);
			}

			// AF vinculada com situação Vincular ao Contrato = NÃO
			// Deve-se realizar a remoção
			if (aFVO.isJaVinculado()
					&& aFVO.getVincularAoContrato().equals(DominioSimNao.N)) {
				ScoAfContrato afContratoARemover = scoAfContratosDAO.obterAfContratoByAfContrato(contrato,
						autFornecimentoFacade.obterAfByNumero(aFVO.getAf().getNumero()));
				
				scoAfContratosDAO.remover(afContratoARemover);

				registrarAfContratoJn(afContratoARemover, DominioOperacoesJournal.DEL);
				
				contrato.setValorTotal(contrato.getValorTotal().subtract(
						aFVO.getValorProposta()));
				if (contrato.getSituacao().equals(DominioSituacaoEnvioContrato.E)){
					contrato.setSituacao(DominioSituacaoEnvioContrato.AR);
				}
				
				ScoContratoDAO scoContratoDAO = this.getScoContratoDAO();
				scoContratoDAO.atualizar(contrato);
				scoContratoDAO.flush();
								
				aFVO.getAf().setNroContrato(null);
				aFVO.getAf().setDtVenctoContrato(null);
				autFornecimentoFacade.atualizarAutorizacaoForn(aFVO.getAf(), true);
			} else {
				// AF já vinculada com situação Vincular ao Contrato = SIM
				// deve-se apenas atualizar o campo Version				
				if (aFVO.isJaVinculado()
						&& aFVO.getVincularAoContrato().equals(DominioSimNao.S)) {
					ScoAfContrato scoAfContrato = scoAfContratosDAO
							.obterAfContratoByAfContrato(
									contrato,
									autFornecimentoFacade.obterAfByNumero(
											aFVO.getAf().getNumero()));
					
					scoAfContrato.setAlteradoEm(new Date());
					scoAfContrato.setVersion(scoAfContrato.getVersion() + 1);
					scoAfContratosDAO.atualizar(scoAfContrato);
					scoAfContratosDAO.flush();
				}
			}
		}

	}
	
	private void registrarAfContratoJn(ScoAfContrato afContrato, DominioOperacoesJournal operacao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		ScoAfContratoJn afContratoJn = BaseJournalFactory.getBaseJournal(operacao, ScoAfContratoJn.class, servidorLogado.getUsuario());
		
		afContratoJn.setSeq(afContrato.getSeq());
		afContratoJn.setScoAutorizacoesForn(afContrato.getScoAutorizacoesForn());
		afContratoJn.setScoContrato(afContrato.getScoContrato());
		
		getScoAfContratoJnDAO().persistir(afContratoJn);
		getScoAfContratoJnDAO().flush();
	}

	
	// Não podem ser retiradas todas AFs do contrato
	public void validaExistenciaAfVinculada(
			List<AutorizacaoFornecimentoVO> listaAutorizacaoFornecimentoVO)
			throws BaseException {
		boolean existeAfVinculada = false;

		for (AutorizacaoFornecimentoVO aFVO : listaAutorizacaoFornecimentoVO) {
			if (aFVO.getVincularAoContrato().equals(DominioSimNao.S)) {
				existeAfVinculada = true;
			}
		}

		if (!existeAfVinculada) {
			throw new BaseException(
					ManterAfContratoONExceptionCode.MENSAGEM_NECESSARIO_EXISTIR_AF_VINCULADA);
		}
	}

	public List<AutorizacaoFornecimentoVO> listarAfTheFornLicContrato(
			ScoContrato contrato) {
		List<ScoAutorizacaoForn> listaAutorizacaoForn;
		List<ScoAfContrato> listaAfContrato;
		List<AutorizacaoFornecimentoVO> listaAutorizacaoFornecimentoVO = new ArrayList<AutorizacaoFornecimentoVO>();
		String cpfCnpj;

		cpfCnpj = getCpfCnpj(contrato);

		// Lista das autorizações de fornecimento já vinculas ao contrato
		listaAfContrato = getScoAfContratosDAO().obterAfByContrato(contrato);
		for (ScoAfContrato afContrato : listaAfContrato) {
			AutorizacaoFornecimentoVO autorizacaoFornecimentoVO = new AutorizacaoFornecimentoVO();

			autorizacaoFornecimentoVO.setNumeroAf(afContrato
					.getScoAutorizacoesForn().getPropostaFornecedor()
					.getLicitacao().getNumero());
			autorizacaoFornecimentoVO.setNroComplemento(afContrato
					.getScoAutorizacoesForn().getNroComplemento());
			autorizacaoFornecimentoVO.setCgcCpf(cpfCnpj);
			autorizacaoFornecimentoVO.setRazaoSocial(contrato.getFornecedor()
					.getRazaoSocial());
			autorizacaoFornecimentoVO
					.setValorProposta(getValorProposta(afContrato
							.getScoAutorizacoesForn()));
			autorizacaoFornecimentoVO.setModalidadeEmpenho(afContrato
					.getScoAutorizacoesForn().getModalidadeEmpenho()
					.getDescricao());
			autorizacaoFornecimentoVO.setConvenioFinanceiro(afContrato
					.getScoAutorizacoesForn().getConvenioFinanceiro().getDescricao());
			autorizacaoFornecimentoVO.setFrequenciaEntrega(afContrato
					.getScoAutorizacoesForn().getPropostaFornecedor()
					.getLicitacao().getFrequenciaEntrega());
			
			for (ScoItemAutorizacaoForn item : afContrato.getScoAutorizacoesForn().getItensAutorizacaoForn()) {
				Hibernate.initialize(item.getItemPropostaFornecedor());
				if(item.getItemPropostaFornecedor() != null){
					Hibernate.initialize(item.getItemPropostaFornecedor().getUnidadeMedida());
				}
				Hibernate.initialize(item.getScoFaseSolicitacao());
				for (ScoFaseSolicitacao fase : item.getScoFaseSolicitacao()) {
					Hibernate.initialize(fase.getSolicitacaoDeCompra());
					if (fase.getSolicitacaoDeCompra() != null) {
						Hibernate.initialize(fase.getSolicitacaoDeCompra().getMaterial());
					}
					Hibernate.initialize(fase.getSolicitacaoServico());
					if (fase.getSolicitacaoServico() != null) {
						Hibernate.initialize(fase.getSolicitacaoServico().getServico());
					}
				}
			}
			autorizacaoFornecimentoVO.setAf(afContrato.getScoAutorizacoesForn());
			autorizacaoFornecimentoVO.setVincularAoContrato(DominioSimNao.S);
			autorizacaoFornecimentoVO.setJaVinculado(true);
			
			autorizacaoFornecimentoVO.setItensAF(buscaItensAFVO(afContrato.getScoAutorizacoesForn()));
			
			listaAutorizacaoFornecimentoVO.add(autorizacaoFornecimentoVO);
		}

		if (contrato.getLicitacao() != null && contrato.getFornecedor() != null) {
			listaAutorizacaoForn = this.getAutFornecimentoFacade()
					.listarAfByFornAndLic(contrato.getLicitacao(),
							contrato.getFornecedor());

			// Lista de autorizações de fornecimento da licitação e fornecedor
			// ainda não vinculadas ao contrato
			for (ScoAutorizacaoForn autorizacaoForn : listaAutorizacaoForn) {
				AutorizacaoFornecimentoVO autorizacaoFornecimentoVO = new AutorizacaoFornecimentoVO();

				autorizacaoFornecimentoVO.setNumeroAf(autorizacaoForn
						.getPropostaFornecedor().getLicitacao().getNumero());
				autorizacaoFornecimentoVO.setNroComplemento(autorizacaoForn
						.getNroComplemento());
				autorizacaoFornecimentoVO.setCgcCpf(cpfCnpj);
				autorizacaoFornecimentoVO.setRazaoSocial(contrato
						.getFornecedor().getRazaoSocial());
				autorizacaoFornecimentoVO
						.setValorProposta(getValorProposta(autorizacaoForn));
				autorizacaoFornecimentoVO.setModalidadeEmpenho(autorizacaoForn
						.getModalidadeEmpenho().getDescricao());
				autorizacaoFornecimentoVO.setConvenioFinanceiro(autorizacaoForn
						.getConvenioFinanceiro().getDescricao());
				autorizacaoFornecimentoVO.setFrequenciaEntrega(autorizacaoForn
						.getPropostaFornecedor().getLicitacao()
						.getFrequenciaEntrega());
				autorizacaoFornecimentoVO.setAf(autorizacaoForn);
				autorizacaoFornecimentoVO
						.setVincularAoContrato(DominioSimNao.N);
				autorizacaoFornecimentoVO.setJaVinculado(false);
				
				autorizacaoFornecimentoVO.setItensAF(buscaItensAFVO(autorizacaoForn));

				listaAutorizacaoFornecimentoVO.add(autorizacaoFornecimentoVO);
			}
		}

		return listaAutorizacaoFornecimentoVO;
	}
	
	public List<ItemAutorizacaoFornVO> buscaItensAFVO(ScoAutorizacaoForn af) {
		List<ItemAutorizacaoFornVO> listaItensAFVO = new ArrayList<ItemAutorizacaoFornVO>();
		for(ScoItemAutorizacaoForn iprop : af.getItensAutorizacaoForn()){
			for(ScoFaseSolicitacao fas : iprop.getScoFaseSolicitacao()){
				ItemAutorizacaoFornVO vo = new ItemAutorizacaoFornVO();
				vo.setFreq(af.getPropostaFornecedor().getLicitacao().getFrequenciaEntrega());
				if(fas.getSolicitacaoDeCompra()!=null) {
					vo.setMaterial(fas.getSolicitacaoDeCompra().getMaterial());
				}
				if(fas.getSolicitacaoServico()!=null) {
					vo.setServico(fas.getSolicitacaoServico().getServico());
				}
				vo.setNumItem(iprop.getId().getNumero());
				vo.setQuant(iprop.getItemPropostaFornecedor().getQuantidade().intValue());
				vo.setUnidade(iprop.getItemPropostaFornecedor().getUnidadeMedida());
				vo.setValorUnit(iprop.getItemPropostaFornecedor().getValorUnitario());
				listaItensAFVO.add(vo);
				}
		}

		return listaItensAFVO;
	}
	

	/**
	 * Gets the cpf cnpj.
	 * 
	 * @return the cpf cnpj
	 */
	public String getCpfCnpj(ScoContrato contrato) {
		if (contrato != null && contrato.getFornecedor() != null
				&& contrato.getFornecedor().getCgc() != null) {
			return CoreUtil.formatarCNPJ(contrato.getFornecedor().getCgc());
		} else if (contrato != null && contrato.getFornecedor() != null
				&& contrato.getFornecedor().getCpf() != null) {
			return CoreUtil.formataCPF(contrato.getFornecedor().getCpf());
		}
		return "";
	}

	public BigDecimal getValorProposta(ScoAutorizacaoForn autorizacaoForn) {
		BigDecimal valorProposta;
		BigDecimal valorAuxiliar;
		List<ScoItemAutorizacaoForn> itensProp = autorizacaoForn
				.getItensAutorizacaoForn();

		valorProposta = BigDecimal.ZERO ;
		valorAuxiliar = BigDecimal.ZERO ;
		
		for (ScoItemAutorizacaoForn iprop : itensProp) {
			if (autorizacaoForn.getPropostaFornecedor().getLicitacao()
					.getFrequenciaEntrega() != null) {
				valorAuxiliar = BigDecimal
						.valueOf(
								iprop.getItemPropostaFornecedor()
										.getQuantidade())
						.multiply(
								BigDecimal
										.valueOf(
												autorizacaoForn
														.getPropostaFornecedor()
														.getLicitacao()
														.getFrequenciaEntrega())
										.multiply(
												iprop.getItemPropostaFornecedor()
														.getValorUnitario()
														.setScale(
																2,
																BigDecimal.ROUND_HALF_EVEN)));

				valorProposta = valorProposta.add(valorAuxiliar);
				
			} else {
				valorProposta = valorProposta.add(BigDecimal.valueOf(
						iprop.getItemPropostaFornecedor().getQuantidade())
						.multiply(
								iprop.getItemPropostaFornecedor()
										.getValorUnitario()));
			}
		}
		return valorProposta;
	}
	
	// DAOs

	protected ScoAfContratosDAO getScoAfContratosDAO() {
		return scoAfContratosDAO;
	}

	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}

	// Facades
	
	protected IAutFornecimentoFacade getAutFornecimentoFacade(){
		return autFornecimentoFacade;
	}
	
	protected ScoAfContratoJnDAO getScoAfContratoJnDAO(){
		return scoAfContratoJnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
