package br.gov.mec.aghu.sicon.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.dominio.DominioOrigemContrato;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioTipoItemContrato;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.model.ScoAfContrato;
import br.gov.mec.aghu.model.ScoContrato;
import br.gov.mec.aghu.model.ScoConvItensContrato;
import br.gov.mec.aghu.model.ScoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemAutorizacaoForn;
import br.gov.mec.aghu.model.ScoItensContrato;
import br.gov.mec.aghu.sicon.dao.ScoAditContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoAfContratosDAO;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoConvItensContratoDAO;
import br.gov.mec.aghu.sicon.dao.ScoItensContratoDAO;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@SuppressWarnings({ "PMD.CyclomaticComplexity", "PMD.AtributoEmSeamContextManager" })
@Stateless
public class GerenciarContratosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(GerenciarContratosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private ScoConvItensContratoDAO scoConvItensContratoDAO;

	@Inject
	private ScoContratoDAO scoContratoDAO;

	@Inject
	private ScoItensContratoDAO scoItensContratoDAO;

	@Inject
	private ScoAfContratosDAO scoAfContratosDAO;

	@Inject
	private ScoAditContratoDAO scoAditContratoDAO;

	private static final long serialVersionUID = -4327135150258159174L;

	@SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
	public List<ScoContrato> obterContratoByFiltro(ContratoFiltroVO input) {
		List<ScoContrato> listContratos = scoContratoDAO.obterContratoByFiltro(input);

		// Tratamento do filtro de itens
		Set<ScoContrato> listContratosFinal = null;
		for (ScoContrato contrato : listContratos) {

//			if (input.getAf().getNumero() == null && input.getAf().getNroComplemento() == null) {
				contrato.setScoAfContratos(scoAfContratosDAO.obterAfByContrato(contrato));
//			}
			if (input.getSitEnvAditivo() != null) {
				contrato.setAditivos(scoAditContratoDAO.obterAditivosPorContratoSituacao(contrato, input.getSitEnvAditivo()));
			} else {
				Hibernate.initialize(contrato.getAditivos());
			}

			/*
			 * if (input.getTipoItens() != null || input.getGrupoMaterial() !=
			 * null || input.getEstocavel() != null || input.getMaterial() !=
			 * null || input.getServico() != null || input.getGrupoServico() !=
			 * null || input.getContrato().getDtInicioVigencia() != null ||
			 * input.getContrato().getDtFimVigencia() != null) {
			 */
			listContratosFinal = new HashSet<ScoContrato>();

			if (contrato.getIndOrigem() == DominioOrigemContrato.A) {
				// se for contrato automatico
				for (ScoAfContrato afcont : contrato.getScoAfContratos()) {
					for (ScoItemAutorizacaoForn itensaf : afcont.getScoAutorizacoesForn().getItensAutorizacaoForn()) {

						// filtra tipo de itens da af
						if (input.getTipoItens() != null) {
							if (verificaContratoAutomaticoPorTipoItem(contrato, input.getTipoItens())) {
								listContratosFinal.add(contrato);
							}
						}
						// filtra materiais estocaveis
						if (input.getEstocavel() != null) {
							if (checkEstocRestrAut(itensaf.getScoFaseSolicitacao(), input.getEstocavel())) {
								listContratosFinal.add(contrato);
							}
						}
						for (ScoFaseSolicitacao fase : itensaf.getScoFaseSolicitacao()) {
							
//							Hibernate.initialize(fase.getSolicitacaoServico());
//							if(fase.getSolicitacaoServico() != null){
//								Hibernate.initialize(fase.getSolicitacaoServico().getServico());
//								if(fase.getSolicitacaoServico().getServico() != null){
//									Hibernate.initialize(fase.getSolicitacaoServico().getServico().getServicoSicon());
//								}
//							}
							
//							Hibernate.initialize(fase.getSolicitacaoDeCompra());
//							if(fase.getSolicitacaoDeCompra() != null){
//								Hibernate.initialize(fase.getSolicitacaoDeCompra().getMaterial());
//								if(fase.getSolicitacaoDeCompra().getMaterial() != null){
//									Hibernate.initialize(fase.getSolicitacaoDeCompra().getMaterial().getMateriaisSicon());
//								}
//							}
							
							// filtro grupo serv
							if (input.getGrupoServico() != null) {
								if (fase.getSolicitacaoServico() != null
										&& fase.getSolicitacaoServico().getServico().getGrupoServico().equals(input.getGrupoServico())) {
									listContratosFinal.add(contrato);
								}
							}
							// filtro serv
							if (input.getServico() != null) {
								if (fase.getSolicitacaoServico() != null
										&& fase.getSolicitacaoServico().getServico().equals(input.getServico())) {
									listContratosFinal.add(contrato);
								}
							}
							// filtro grupo mat
							if (input.getGrupoMaterial() != null) {
								if (fase.getSolicitacaoDeCompra() != null
										&& fase.getSolicitacaoDeCompra().getMaterial().getGrupoMaterial().equals(input
												.getGrupoMaterial())) {
									listContratosFinal.add(contrato);
								}
							}
							// filtro mat
							if (input.getMaterial() != null) {
								if (fase.getSolicitacaoDeCompra() != null
										&& fase.getSolicitacaoDeCompra().getMaterial().equals(input.getMaterial())) {
									listContratosFinal.add(contrato);
								}
							}
						}
					}
				}
				// se for contrato manual
			} else {
				// cont manual pode ter itens.size =0
				if (contrato.getItensContrato().size() == 0) {
					continue;
				} else {
					// tipo de itens
					if (input.getTipoItens() != null) {
						if (checkMatRestrMan(contrato.getItensContrato(), input.getTipoItens())) {
							listContratosFinal.add(contrato);
						}
					}
					// estocavel?
					if (input.getEstocavel() != null) {
						if (checkEstocRestrMan(contrato.getItensContrato(), input.getEstocavel())) {
							listContratosFinal.add(contrato);
						}
					}
					for (ScoItensContrato i : contrato.getItensContrato()) {
						
						if (i.getServico() != null) {
							
//							Hibernate.initialize(i.getServico().getServicoSicon());
//							Hibernate.initialize(i.getServico().getGrupoServico());
							
							// filtro grupo serv
							if (input.getGrupoServico() != null) {
								if (i.getServico().getGrupoServico() != null
										&& i.getServico().getGrupoServico().equals(input.getGrupoServico())) {
									listContratosFinal.add(contrato);
								}
							}
							// filtro serv
							if (input.getServico() != null) {
								if (i.getServico().equals(input.getServico())) {
									listContratosFinal.add(contrato);
								}
							}
						}
						
						if (i.getMaterial() != null) {
//							Hibernate.initialize(i.getMaterial().getMateriaisSicon());
//							Hibernate.initialize(i.getMaterial().getGrupoMaterial());

							// filtro grupo mat
							if (input.getGrupoMaterial() != null) {
								if (i.getMaterial().getGrupoMaterial() != null
										&& i.getMaterial().getGrupoMaterial().equals(input.getGrupoMaterial())) {
									listContratosFinal.add(contrato);
								}
							}
							// filtro mat
							if (input.getMaterial() != null) {
								if (i.getMaterial().equals(input.getMaterial())) {
									listContratosFinal.add(contrato);
								}
							}
						}
						
					}
				}
			}

			// Tratamento do filtro por período de vigência
			if (input.getContrato().getDtInicioVigencia() != null || input.getContrato().getDtFimVigencia() != null) {
				if (contrato.getAditivos().size() > 0) {
					List<ScoAditContrato> aditsDataMod = (List<ScoAditContrato>) CollectionUtils.select(contrato.getAditivos(),
							new Predicate() {
								@Override
								public boolean evaluate(Object p) {
									ScoAditContrato a = (ScoAditContrato) p;
									return (a.getDtInicioVigencia() != null && a.getDtFimVigencia() != null);
								}
							});
					if (aditsDataMod != null && aditsDataMod.size() > 0) {
						ScoAditContrato cx = aditsDataMod.get(0);
						Date aditdate = cx.getCriadoEm();
						for (ScoAditContrato ax : aditsDataMod) {
							if (ax.getDtInicioVigencia().after(aditdate)) {
								aditdate = ax.getDtInicioVigencia();
								cx = ax;
							} else {
								continue;
							}
						}
						if (input.getContrato().getDtInicioVigencia() != null
								&& input.getContrato().getDtFimVigencia() == null
								&& (cx.getDtInicioVigencia().after(input.getContrato().getDtInicioVigencia()) || cx
										.getDtInicioVigencia().equals(input.getContrato().getDtInicioVigencia()))) {
							listContratosFinal.add(contrato);
						} else if (input.getContrato().getDtFimVigencia() != null
								&& input.getContrato().getDtInicioVigencia() == null
								&& (cx.getDtFimVigencia().before(input.getContrato().getDtFimVigencia()) || cx.getDtFimVigencia()
										.equals(input.getContrato().getDtFimVigencia()))) {
							listContratosFinal.add(contrato);
						} else if (input.getContrato().getDtInicioVigencia() != null
								&& input.getContrato().getDtFimVigencia() != null
								&& (cx.getDtInicioVigencia().after(input.getContrato().getDtInicioVigencia()) || cx
										.getDtInicioVigencia().equals(input.getContrato().getDtInicioVigencia()))
								&& (cx.getDtFimVigencia().before(input.getContrato().getDtFimVigencia()) || cx.getDtFimVigencia()
										.equals(input.getContrato().getDtFimVigencia()))) {
							listContratosFinal.add(contrato);
						}
					}
				} else {
					if (input.getContrato().getDtInicioVigencia() != null
							&& input.getContrato().getDtFimVigencia() == null
							&& (contrato.getDtInicioVigencia().after(input.getContrato().getDtInicioVigencia()) || contrato
									.getDtInicioVigencia().equals(input.getContrato().getDtInicioVigencia()))) {
						listContratosFinal.add(contrato);
					} else if (input.getContrato().getDtFimVigencia() != null
							&& input.getContrato().getDtInicioVigencia() == null
							&& (contrato.getDtFimVigencia().before(input.getContrato().getDtFimVigencia()) || contrato
									.getDtFimVigencia().equals(input.getContrato().getDtFimVigencia()))) {
						listContratosFinal.add(contrato);
					} else if (input.getContrato().getDtInicioVigencia() != null
							&& input.getContrato().getDtFimVigencia() != null
							&& (contrato.getDtInicioVigencia().after(input.getContrato().getDtInicioVigencia()) || contrato
									.getDtInicioVigencia().equals(input.getContrato().getDtInicioVigencia()))
							&& (contrato.getDtFimVigencia().before(input.getContrato().getDtFimVigencia()) || contrato
									.getDtFimVigencia().equals(input.getContrato().getDtFimVigencia()))) {
						listContratosFinal.add(contrato);
					}
				}

			}
		}

		if (listContratosFinal != null && listContratosFinal.size() > 0) {
			return new ArrayList<ScoContrato>(listContratosFinal);
		} else {
			return listContratos;
		}

	}

	public void deletarContrato(ScoContrato input) throws ApplicationBusinessException {

		if (input.getIndOrigem() == DominioOrigemContrato.A) {
			for (ScoAfContrato afc : input.getScoAfContratos()) {
				scoAfContratosDAO.refresh(afc);
				scoAfContratosDAO.remover(afc);
				scoAfContratosDAO.flush();
			}

			scoContratoDAO.removerPorId(input.getSeq());

			scoContratoDAO.flush();

		} else {

			for (ScoItensContrato itens : input.getItensContrato()) {
				for (ScoConvItensContrato convitens : itens.getConvItensContrato()) {
					scoConvItensContratoDAO.refresh(convitens);
					scoConvItensContratoDAO.remover(convitens);
					scoConvItensContratoDAO.flush();
				}
				scoItensContratoDAO.refresh(itens);
				scoItensContratoDAO.remover(itens);
				scoItensContratoDAO.flush();

			}
			scoContratoDAO.refresh(input);
			scoContratoDAO.remover(input);
			scoContratoDAO.flush();
		}
	}

	private Boolean verificaContratoAutomaticoPorTipoItem(ScoContrato contrato, DominioTipoItemContrato tipoItem) {
		for (ScoAfContrato afContratos : contrato.getScoAfContratos()) {

			for (ScoItemAutorizacaoForn iprop : afContratos.getScoAutorizacoesForn().getItensAutorizacaoForn()) {
				for (ScoFaseSolicitacao fase : iprop.getScoFaseSolicitacao()) {
					// Contrato possui Material, mas a pesquisa filtra por
					// Contratos que possuem somente Serviços
					if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial() != null
							&& tipoItem == DominioTipoItemContrato.S) {

						return false;
					}

					// Contrato possui Serviço, mas a pesquisa filtra por
					// Contratos que possuem somente Materiais
					if (fase.getSolicitacaoServico() != null && fase.getSolicitacaoServico().getServico() != null
							&& tipoItem == DominioTipoItemContrato.M) {
						return false;
					}
				}
			}
		}

		return true;
	}

	private Boolean checkEstocRestrAut(List<ScoFaseSolicitacao> input, DominioSimNao flg) {
		if (flg == DominioSimNao.S) {
			for (ScoFaseSolicitacao fase : input) {
				if (fase.getSolicitacaoDeCompra() != null && fase.getSolicitacaoDeCompra().getMaterial().getIndEstocavelBoolean()) {
					continue;
				} else {
					return false;
				}
			}
		} else {
			for (ScoFaseSolicitacao fase : input) {
				if (fase.getSolicitacaoDeCompra() != null
						&& !fase.getSolicitacaoDeCompra().getMaterial().getIndEstocavelBoolean()) {
					continue;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private Boolean checkMatRestrMan(List<ScoItensContrato> input, DominioTipoItemContrato flg) {
		if (flg == DominioTipoItemContrato.M) {
			for (ScoItensContrato i : input) {
				if (i.getMaterial() != null && i.getServico() == null) {
					continue;
				} else {
					return false;
				}
			}
		} else {
			for (ScoItensContrato i : input) {
				if (i.getMaterial() == null && i.getServico() != null) {
					continue;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	private Boolean checkEstocRestrMan(List<ScoItensContrato> input, DominioSimNao flg) {
		if (flg == DominioSimNao.S) {
			for (ScoItensContrato i : input) {
				if (i.getMaterial() != null && i.getMaterial().getIndEstocavelBoolean()) {
					continue;
				} else {
					return false;
				}
			}
		} else {
			for (ScoItensContrato i : input) {
				if (i.getMaterial() != null && !i.getMaterial().getIndEstocavelBoolean()) {
					continue;
				} else {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Retorna a quantidades de materiais e/ou serviços pendentes de codigo sicon de acordo
	 * com a origem do contrato.
	 * 
	 * @param contrato
	 * @return
	 */
	public Long siconPendentesCount(ScoContrato contrato) {
		Long result = null;
		
		if (DominioOrigemContrato.A.equals(contrato.getIndOrigem())) {
			Long materiais = scoContratoDAO
					.materiaisSiconPendentesAutomaticoCount(contrato);
			Long servicos = scoContratoDAO.servicosSiconPendentesAutomaticoCount(contrato);
			result = materiais.longValue() + servicos.longValue();
			
		} else {
			Long materiais = scoContratoDAO
					.materiaisSiconPendentesManualCount(contrato);
			Long servicos = scoContratoDAO.servicosSiconPendentesManualCount(contrato);
			result = materiais.longValue() + servicos.longValue();
		}
		
		return result;
	}

}
