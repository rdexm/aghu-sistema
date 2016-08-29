package br.gov.mec.aghu.sig.custos.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigDirecionadores;
import br.gov.mec.aghu.model.SigObjetoCustoCcts;
import br.gov.mec.aghu.model.SigObjetoCustoClientes;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.model.SigObjetoCustoDirRateios;
import br.gov.mec.aghu.model.SigObjetoCustoHistoricos;
import br.gov.mec.aghu.model.SigObjetoCustoPhis;
import br.gov.mec.aghu.model.SigObjetoCustoVersoes;
import br.gov.mec.aghu.sig.dao.SigAtividadeEquipamentosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeInsumosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoasDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeServicosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoHistoricosDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoVersoesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustosDAO;

@Stateless
public class AnaliseHistoricoON extends BaseBusiness {

	private static final String COM_DIRECIONADOR_DE = " com direcionador de ";

	private static final String FOI_DESATIVADO = " foi desativado.";

	private static final String STRING = " - ";

	private static final String PARA = " para ";

	private static final Log LOG = LogFactory.getLog(AnaliseHistoricoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private SigAtividadeServicosDAO sigAtividadeServicosDAO;

	@Inject
	private SigObjetoCustoHistoricosDAO sigObjetoCustoHistoricosDAO;

	@Inject
	private SigAtividadesDAO sigAtividadesDAO;

	@Inject
	private SigAtividadeInsumosDAO sigAtividadeInsumosDAO;

	@Inject
	private SigObjetoCustosDAO sigObjetoCustosDAO;

	@Inject
	private SigAtividadePessoasDAO sigAtividadePessoasDAO;

	@Inject
	private SigObjetoCustoVersoesDAO sigObjetoCustoVersoesDAO;

	@Inject
	private SigAtividadeEquipamentosDAO sigAtividadeEquipamentosDAO;

	private static final long serialVersionUID = 2916231442962728811L;

	private static final String ATIVIDADE_PESSOAS = "atividadePessoas";
	private static final String ATIVIDADE_EQUIPAMENTOS = "atividadeEquipamentos";
	private static final String ATIVIDADE_SERVICOS = "atividadeServicos";
	private static final String ATIVIDADE_INSUMOS = "atividadeInsumos";
	private static final String ATIVIDADE = "atividade";
	private static final String OC_PHIS = "objetoCustoPhis";
	private static final String OC_CCTS = "objetoCustoCcts";
	private static final String OC_CLIENTES = "objetoCustoClientes";
	private static final String OC_COMPOSICOES = "objetoCustoComposicoes";
	private static final String OC_VERSAO = "objetoCustoVersao";

	public Map<String, Object> obterAtividadeDesatachado(SigAtividades atividade) {
		Map<String, Object> map = new HashMap<String, Object>();

		SigAtividadePessoasDAO sigAtividadePessoasDAO = getSigAtividadePessoasDAO();
		List<SigAtividadePessoas> listAtividadePessoas = new ArrayList<SigAtividadePessoas>(atividade.getListAtividadePessoas().size());
		for (SigAtividadePessoas sigAtividadePessoas : atividade.getListAtividadePessoas()) {
			sigAtividadePessoas.getTempoMedio();
			sigAtividadePessoasDAO.desatachar(sigAtividadePessoas);
			listAtividadePessoas.add(sigAtividadePessoas);
		}
		map.put(ATIVIDADE_PESSOAS, listAtividadePessoas);

		SigAtividadeEquipamentosDAO sigAtividadeEquipamentosDAO = getSigAtividadeEquipamentosDAO();
		List<SigAtividadeEquipamentos> listAtividadeEquipamentos = new ArrayList<SigAtividadeEquipamentos>(atividade
				.getListAtividadeEquipamentos().size());
		for (SigAtividadeEquipamentos sigAtividadeEquipamentos : atividade.getListAtividadeEquipamentos()) {
			sigAtividadeEquipamentosDAO.desatachar(sigAtividadeEquipamentos);
			listAtividadeEquipamentos.add(sigAtividadeEquipamentos);
		}
		map.put(ATIVIDADE_EQUIPAMENTOS, listAtividadeEquipamentos);

		SigAtividadeServicosDAO sigAtividadeServicosDAO = getSigAtividadeServicosDAO();
		List<SigAtividadeServicos> listAtividadeServicos = new ArrayList<SigAtividadeServicos>(atividade.getListAtividadeServicos().size());
		for (SigAtividadeServicos sigAtividadeServicos : atividade.getListAtividadeServicos()) {
			sigAtividadeServicosDAO.desatachar(sigAtividadeServicos);
			listAtividadeServicos.add(sigAtividadeServicos);
		}
		map.put(ATIVIDADE_SERVICOS, listAtividadeServicos);

		SigAtividadeInsumosDAO sigAtividadeInsumosDAO = getSigAtividadeInsumosDAO();
		List<SigAtividadeInsumos> listAtividadeInsumos = new ArrayList<SigAtividadeInsumos>(atividade.getListAtividadeInsumos().size());
		for (SigAtividadeInsumos sigAtividadeInsumos : atividade.getListAtividadeInsumos()) {
			sigAtividadeInsumosDAO.desatachar(sigAtividadeInsumos);
			listAtividadeInsumos.add(sigAtividadeInsumos);
		}
		map.put(ATIVIDADE_INSUMOS, listAtividadeInsumos);

		this.getSigAtividadesDAO().desatachar(atividade);
		map.put(ATIVIDADE, atividade);
		return map;
	}

	public Map<String, Object> obterObjetoCustoVersoesDesatachado(SigObjetoCustoVersoes objetoCustoVersao) {
		Map<String, Object> map = new HashMap<String, Object>();
		this.getSigObjetoCustoVersoesDAO().refresh(objetoCustoVersao);

		Set<SigObjetoCustoComposicoes> listObjetoCustoComposicoes = objetoCustoVersao.getListObjetoCustoComposicoes();
		map.put(OC_COMPOSICOES, new ArrayList<SigObjetoCustoComposicoes>(listObjetoCustoComposicoes));

		Set<SigObjetoCustoPhis> listObjetoCustoPhis = objetoCustoVersao.getListObjetoCustoPhis();
		map.put(OC_PHIS, new ArrayList<SigObjetoCustoPhis>(listObjetoCustoPhis));

		Set<SigObjetoCustoCcts> listObjetoCustoCcts = objetoCustoVersao.getListObjetoCustoCcts();
		map.put(OC_CCTS, new ArrayList<SigObjetoCustoCcts>(listObjetoCustoCcts));

		Set<SigObjetoCustoDirRateios> listObjetoCustoDirRateios = objetoCustoVersao.getListObjetoCustoDirRateios();
		map.put("objetoCustoClientes", new ArrayList<SigObjetoCustoDirRateios>(listObjetoCustoDirRateios));

		Set<SigObjetoCustoClientes> listObjetoCustoClientes = objetoCustoVersao.getListObjetoCustoClientes();
		map.put(OC_CLIENTES, new ArrayList<SigObjetoCustoClientes>(listObjetoCustoClientes));

		this.getSigObjetoCustosDAO().desatachar(objetoCustoVersao.getSigObjetoCustos());
		this.getSigObjetoCustoVersoesDAO().desatachar(objetoCustoVersao);
		map.put(OC_VERSAO, objetoCustoVersao);
		return map;
	}

	public void iniciaProcessoHistoricoCopiaObjetoCusto(SigObjetoCustoVersoes objetoCustoVersao,
			SigObjetoCustoVersoes objetoCustoVersaoSuggestion, List<SigObjetoCustoComposicoes> copia, RapServidores rapServidores) {

		if (!objetoCustoVersao.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
			return;
		}

		StringBuilder acao = new StringBuilder(68);
		acao.append("Objeto de custo " + objetoCustoVersao.getSeq() + STRING
				+ objetoCustoVersao.getSigObjetoCustos().getNome());

		if (objetoCustoVersao.getListObjetoCustoCcts().size() > 0) {
			acao.append(" do centro de custo "
					+ ((SigObjetoCustoCcts) objetoCustoVersao.getListObjetoCustoCcts().toArray()[0]).getFccCentroCustos().getCodigo());
		}
		acao.append(": incluído um item na composição ");

		SigObjetoCustoHistoricosDAO sigObjetoCustoHistoricosDAO = this.getSigObjetoCustoHistoricosDAO();
		for (SigObjetoCustoComposicoes sigObjetoCustoComposicoes : copia) {
			StringBuilder acaoLocal = new StringBuilder(acao);
			if (sigObjetoCustoComposicoes.getSigAtividades() != null) {
				acaoLocal.append(sigObjetoCustoComposicoes.getSigAtividades().getSeq()).append(' '
						+ sigObjetoCustoComposicoes.getSigAtividades().getNome());
			} else {
				acaoLocal.append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSeq() + " "
						+ sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSigObjetoCustos().getNome() + " "
						+ sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getVersion());
			}

			acaoLocal.append(COM_DIRECIONADOR_DE).append(sigObjetoCustoComposicoes.getSigDirecionadores().getNome())

			.append(" copiado de " + objetoCustoVersaoSuggestion.getSeq() + " "
					+ objetoCustoVersaoSuggestion.getSigObjetoCustos().getNome() + STRING + objetoCustoVersaoSuggestion.getVersion());

			SigObjetoCustoHistoricos sigObjetoCustoHistoricos = new SigObjetoCustoHistoricos();
			sigObjetoCustoHistoricos.setRapServidores(rapServidores);
			sigObjetoCustoHistoricos.setComponente("OC - Composição");
			sigObjetoCustoHistoricos.setSigObjetoCustoVersoes(objetoCustoVersao);
			sigObjetoCustoHistoricos.setCriadoEm(new Date());
			sigObjetoCustoHistoricos.setAcao(acaoLocal.toString());
			sigObjetoCustoHistoricosDAO.persistir(sigObjetoCustoHistoricos);
		}
	}

	public void iniciaProcessoHistoricoProduto(SigObjetoCustoVersoes objetoCustoVersao, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<SigObjetoCustoPhis> listaPhis,
			List<SigObjetoCustoPhis> listaPhisExcluir, RapServidores rapServidores) throws ApplicationBusinessException {
		this.analisaHistoricoProduto(objetoCustoVersao, clone, listaObjetoCustoComposicoes, listaPhis, listaPhisExcluir, rapServidores);
	}

	private void analisaHistoricoProduto(SigObjetoCustoVersoes objetoCustoVersao, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<SigObjetoCustoPhis> listaPhis,
			List<SigObjetoCustoPhis> listaPhisExcluir, RapServidores rapServidores) throws ApplicationBusinessException {
		SigObjetoCustoVersoes objetoCustoClone = (SigObjetoCustoVersoes) clone.get(OC_VERSAO);

		if (objetoCustoVersao == null || objetoCustoClone == null) {
			return;
		}
		if (!objetoCustoVersao.getSeq().equals(objetoCustoClone.getSeq())) {
			return;
		}
		if (!objetoCustoClone.getIndSituacao().equals(DominioSituacaoVersoesCustos.A)) {
			return;
		}

		List<String> acoes = new ArrayList<String>();
		Map<String, String> componente = new HashMap<String, String>();

		StringBuilder acao = new StringBuilder(37);
		acao.append("Objeto de custo ").append(objetoCustoVersao.getSeq()).append(STRING
				).append(objetoCustoVersao.getSigObjetoCustos().getNome());
		
		if (objetoCustoVersao.getListObjetoCustoCcts().size() > 0) {
			acao.append(" do centro de custo "
					+ ((SigObjetoCustoCcts) objetoCustoVersao.getListObjetoCustoCcts().toArray()[0]).getFccCentroCustos().getCodigo()
					+ ": ");
		}

		this.analisaHistoricoGeralProduto(objetoCustoVersao, clone, listaObjetoCustoComposicoes, listaPhis, listaPhisExcluir,
				rapServidores, acoes, objetoCustoClone, componente);
		this.analisaHistoricoComposicao(objetoCustoVersao, clone, listaObjetoCustoComposicoes, listaPhis, listaPhisExcluir, rapServidores,
				acoes, componente);
		this.analisaHistoricoPHI(objetoCustoVersao, clone, listaObjetoCustoComposicoes, listaPhis, listaPhisExcluir, rapServidores, acoes,
				componente);

		SigObjetoCustoHistoricosDAO sigObjetoCustoHistoricosDAO = this.getSigObjetoCustoHistoricosDAO();
		for (String string : acoes) {
			SigObjetoCustoHistoricos sigObjetoCustoHistoricos = new SigObjetoCustoHistoricos();
			sigObjetoCustoHistoricos.setRapServidores(rapServidores);
			sigObjetoCustoHistoricos.setComponente("OC" + STRING + componente.get(string));
			sigObjetoCustoHistoricos.setSigObjetoCustoVersoes(objetoCustoVersao);
			sigObjetoCustoHistoricos.setCriadoEm(new Date());
			sigObjetoCustoHistoricos.setAcao(acao.toString() + string);
			sigObjetoCustoHistoricosDAO.persistir(sigObjetoCustoHistoricos);
		}
	}

	@SuppressWarnings("unchecked")
	private void analisaHistoricoGeralProduto(SigObjetoCustoVersoes objetoCustoVersao, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<SigObjetoCustoPhis> listaPhis,
			List<SigObjetoCustoPhis> listaPhisExcluir, RapServidores rapServidores, List<String> acoes,
			SigObjetoCustoVersoes objetoCustoClone, Map<String, String> componente) {
		StringBuilder acaoGeral = new StringBuilder(73);

		if (!objetoCustoVersao.getSigObjetoCustos().getNome().equals(objetoCustoClone.getSigObjetoCustos().getNome())) {
			acaoGeral.append("nome alterado de " + objetoCustoClone.getSigObjetoCustos().getNome() + PARA
					+ objetoCustoVersao.getSigObjetoCustos().getNome());
		}

		ArrayList<SigObjetoCustoCcts> objetoCustoCctsList = (ArrayList<SigObjetoCustoCcts>) clone.get(OC_CCTS);
		if (objetoCustoCctsList.size() > 0) {
			SigObjetoCustoCcts objetoCustoCcts = objetoCustoCctsList.get(0);

			if (objetoCustoVersao.getListObjetoCustoCcts().size() > 0) {
				if ( compararValores(((SigObjetoCustoCcts) objetoCustoVersao.getListObjetoCustoCcts().toArray()[0]).getFccCentroCustos().getCodigo(), objetoCustoCcts.getFccCentroCustos().getCodigo()) ) {
					if (!acaoGeral.toString().trim().equals("")) {
						acaoGeral.append(", ");
					}
					acaoGeral.append("centro de custo alterado de "
							+ objetoCustoCcts.getFccCentroCustos().getCodigo()
							+ PARA
							+ ((SigObjetoCustoCcts) objetoCustoVersao.getListObjetoCustoCcts().toArray()[0]).getFccCentroCustos()
									.getCodigo());
				}
			}
		}
		if (objetoCustoVersao.getSigObjetoCustos().getIndCompartilha() != objetoCustoClone.getSigObjetoCustos().getIndCompartilha()) {
			if (!acaoGeral.toString().trim().equals("")) {
				acaoGeral.append(", ");
			}
			acaoGeral.append("compartilhamento alterado de " + objetoCustoClone.getSigObjetoCustos().getIndCompartilha() + PARA
					+ objetoCustoVersao.getSigObjetoCustos().getIndCompartilha());
		}
		if (!acaoGeral.toString().trim().equals("")) {
			acoes.add(acaoGeral.toString());
			componente.put(acaoGeral.toString(), "Geral");
		}
	}

	@SuppressWarnings("unchecked")
	private void analisaHistoricoComposicao(SigObjetoCustoVersoes objetoCustoVersao, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<SigObjetoCustoPhis> listaPhis,
			List<SigObjetoCustoPhis> listaPhisExcluir, RapServidores rapServidores, List<String> acoes, Map<String, String> componente) {
		ArrayList<SigObjetoCustoComposicoes> arrayListComposicao = (ArrayList<SigObjetoCustoComposicoes>) clone.get(OC_COMPOSICOES);
		for (SigObjetoCustoComposicoes sigObjetoCustoComposicoes : listaObjetoCustoComposicoes) {
			if (sigObjetoCustoComposicoes.getSeq() == null) {
				StringBuilder acaoLocal = new StringBuilder("incluído um item na composição ");

				if (sigObjetoCustoComposicoes.getSigAtividades() != null) {
					acaoLocal.append(sigObjetoCustoComposicoes.getSigAtividades().getSeq()).append(' '
							).append(sigObjetoCustoComposicoes.getSigAtividades().getNome());
				} else {
					acaoLocal.append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSeq()).append(' '
							).append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSigObjetoCustos().getNome()).append(' '
									).append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getVersion());
				}
				if (objetoCustoVersao.getSigObjetoCustos().getIndTipo().equals(DominioTipoObjetoCusto.AS)) {
					acaoLocal.append(COM_DIRECIONADOR_DE + sigObjetoCustoComposicoes.getSigDirecionadores().getNome()).append('.');
				}

				acoes.add(acaoLocal.toString());
				componente.put(acaoLocal.toString(), "Composição");
			} else {
				SigObjetoCustoComposicoes antigo = arrayListComposicao.get(arrayListComposicao.indexOf(sigObjetoCustoComposicoes));
				StringBuilder acaoLocal = new StringBuilder(17);
				if (sigObjetoCustoComposicoes.getSigDirecionadores() != null && antigo.getSigDirecionadores() != null
						&& compararValores(sigObjetoCustoComposicoes.getSigDirecionadores().getSeq(), antigo.getSigDirecionadores().getSeq())) {
					acaoLocal.append(" direcionador alterado de " + antigo.getSigDirecionadores().getNome() + PARA
							+ sigObjetoCustoComposicoes.getSigDirecionadores().getNome());
				}

				if (compararValores(sigObjetoCustoComposicoes.getNroExecucoes(), antigo.getNroExecucoes())) {
					if (!acaoLocal.toString().trim().equals("")) {
						acaoLocal.append(", ");
					}
					acaoLocal.append(" qtde de execuções alterado de " + formatarValor(antigo.getNroExecucoes()) + PARA
							+ formatarValor(sigObjetoCustoComposicoes.getNroExecucoes()));
				}

				if (compararValores(sigObjetoCustoComposicoes.getIdentificadorPop(), antigo.getIdentificadorPop())) {
					if (!acaoLocal.toString().trim().equals("")) {
						acaoLocal.append(", ");
					}

					acaoLocal.append(" POP alterado de " + formatarValor(antigo.getIdentificadorPop()) + PARA
							+ formatarValor(sigObjetoCustoComposicoes.getIdentificadorPop()));
				}
				if (!acaoLocal.toString().trim().equals("")) {
					StringBuilder string = new StringBuilder("Alterado item da composição ");
					if (sigObjetoCustoComposicoes.getSigAtividades() != null) {
						string.append(sigObjetoCustoComposicoes.getSigAtividades().getSeq()).append(' '
								+ sigObjetoCustoComposicoes.getSigAtividades().getNome());
					} else {
						string.append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSeq()).append(' '
								).append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSigObjetoCustos().getNome()).append(' '
										).append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getVersion());
					}
					acoes.add(string.toString() + acaoLocal.toString() + ".");
					componente.put(string.toString() + acaoLocal.toString() + ".", "Composição");
				}
				if (sigObjetoCustoComposicoes.getIndSituacao() != antigo.getIndSituacao()) {
					StringBuilder acaoLocal1 = new StringBuilder("Item da composição ");

					if (sigObjetoCustoComposicoes.getSigAtividades() != null) {
						acaoLocal1.append(sigObjetoCustoComposicoes.getSigAtividades().getSeq()).append(' ').append(sigObjetoCustoComposicoes.getSigAtividades().getNome());
					} else {
						acaoLocal1.append(sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSeq() + " "
								+ sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getSigObjetoCustos().getNome() + " "
								+ sigObjetoCustoComposicoes.getSigObjetoCustoVersoesCompoe().getVersion());
					}

					if (sigObjetoCustoComposicoes.getIndSituacao().equals(DominioSituacao.A)) {
						acaoLocal1.append(" foi ativado.");
					} else {
						acaoLocal1.append(FOI_DESATIVADO);
					}
					acoes.add(acaoLocal1.toString());
					componente.put(acaoLocal1.toString(), "Composição");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void analisaHistoricoPHI(SigObjetoCustoVersoes objetoCustoVersao, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> listaObjetoCustoComposicoes, List<SigObjetoCustoPhis> listaPhis,
			List<SigObjetoCustoPhis> listaPhisExcluir, RapServidores rapServidores, List<String> acoes, Map<String, String> componente) {
		ArrayList<SigObjetoCustoPhis> arrayListPHI = (ArrayList<SigObjetoCustoPhis>) clone.get(OC_PHIS);
		for (SigObjetoCustoPhis sigObjetoCustoPhis : listaPhis) {
			if (sigObjetoCustoPhis.getSeq() == null) {
				StringBuilder acaoLocal = new StringBuilder("incluído um PHI ");
				acaoLocal.append(sigObjetoCustoPhis.getFatProcedHospInternos().getDescricao()).append('.');
				acoes.add(acaoLocal.toString());
				componente.put(acaoLocal.toString(), "PHI");
			} else {
				SigObjetoCustoPhis antigo = arrayListPHI.get(arrayListPHI.indexOf(sigObjetoCustoPhis));
				if (sigObjetoCustoPhis.getDominioSituacao() != antigo.getDominioSituacao()) {
					String string = "PHI " + sigObjetoCustoPhis.getSeq() + STRING
							+ sigObjetoCustoPhis.getFatProcedHospInternos().getDescricao() + FOI_DESATIVADO;
					acoes.add(string);
					componente.put(string, "PHI");
				}
			}
		}

		for (SigObjetoCustoPhis sigObjetoCustoPhis : listaPhisExcluir) {
			if (sigObjetoCustoPhis.getSeq() != null) {
				StringBuilder acaoLocal = new StringBuilder("PHI ");
				acaoLocal.append(sigObjetoCustoPhis.getSeq() + STRING + sigObjetoCustoPhis.getFatProcedHospInternos().getDescricao()
						+ " foi excluído.");
				acoes.add(acaoLocal.toString());
				componente.put(acaoLocal.toString(), "PHI");
			}
		}
	}

	public void iniciaProcessoHistoricoAtividade(SigAtividades atividade, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> composicoes, List<SigAtividadePessoas> listaPessoas,
			List<SigAtividadeEquipamentos> listEquipamentoAtividade, List<SigAtividadeInsumos> listAtividadeInsumos,
			List<SigAtividadeServicos> listaServicos, RapServidores rapServidores) throws ApplicationBusinessException {

		this.analisaHistoricoAtividade(atividade, clone, composicoes, listaPessoas, listEquipamentoAtividade, listAtividadeInsumos,
				listaServicos, rapServidores);
	}

	private void analisaHistoricoAtividade(SigAtividades atividade, Map<String, Object> clone, List<SigObjetoCustoComposicoes> composicoes,
			List<SigAtividadePessoas> listaPessoas, List<SigAtividadeEquipamentos> listEquipamentoAtividade,
			List<SigAtividadeInsumos> listAtividadeInsumos, List<SigAtividadeServicos> listaServicos, RapServidores rapServidores)
			throws ApplicationBusinessException {

		SigAtividades atividadeClone = (SigAtividades) clone.get(ATIVIDADE);

		if (atividade == null || atividadeClone == null) {
			return;
		}

		if (!atividade.getSeq().equals(atividadeClone.getSeq())) {
			return;
		}

		if (composicoes == null || composicoes.size() == 0) {
			return;
		}

		List<String> acoes = new ArrayList<String>();
		Map<String, String> componente = new HashMap<String, String>();

		StringBuilder acao = new StringBuilder(61);
		acao.append("Atividade ").append(atividade.getSeq()).append(STRING).append(atividade.getNome())
		.append(", associada ao objeto de custo :objetoCustoVersao");

		if (atividade.getSigAtividadeCentroCustos().getFccCentroCustos().getCodigo() != null) {
			acao.append(", do centro de custo " + atividade.getSigAtividadeCentroCustos().getFccCentroCustos().getCodigo() + ": ");
		} else {
			acao.append(": ");
		}

		this.analisaHistoricoGeralAtividade(atividade, clone, composicoes, listaPessoas, listEquipamentoAtividade, listAtividadeInsumos,
				listaServicos, rapServidores, acoes, atividadeClone, componente);
		this.analisaHistoricoPessoas(atividadeClone, clone, composicoes, listaPessoas, listEquipamentoAtividade, listAtividadeInsumos,
				listaServicos, rapServidores, acoes, componente);
		this.analisaHistoricoInsumos(atividadeClone, clone, composicoes, listaPessoas, listEquipamentoAtividade, listAtividadeInsumos,
				listaServicos, rapServidores, acoes, componente);
		this.analisaHistoricoEquipamentos(atividadeClone, clone, composicoes, listaPessoas, listEquipamentoAtividade, listAtividadeInsumos,
				listaServicos, rapServidores, acoes, componente);
		this.analisaHistoricoServicos(atividadeClone, clone, composicoes, listaPessoas, listEquipamentoAtividade, listAtividadeInsumos,
				listaServicos, rapServidores, acoes, componente);

		SigObjetoCustoHistoricosDAO sigObjetoCustoHistoricosDAO = this.getSigObjetoCustoHistoricosDAO();
		for (SigObjetoCustoComposicoes sigObjetoCustoComposicoes : composicoes) {
			String acaoParcial = acao.toString().replace(
					":objetoCustoVersao",
					sigObjetoCustoComposicoes.getSigObjetoCustoVersoes().getSigObjetoCustos().getNome() + STRING
							+ sigObjetoCustoComposicoes.getSigObjetoCustoVersoes().getNroVersao());
			for (String string : acoes) {
				SigObjetoCustoHistoricos sigObjetoCustoHistoricos = new SigObjetoCustoHistoricos();
				sigObjetoCustoHistoricos.setRapServidores(rapServidores);
				sigObjetoCustoHistoricos.setComponente(ATIVIDADE + STRING + componente.get(string));
				sigObjetoCustoHistoricos.setSigObjetoCustoVersoes(sigObjetoCustoComposicoes.getSigObjetoCustoVersoes());
				sigObjetoCustoHistoricos.setCriadoEm(new Date());
				sigObjetoCustoHistoricos.setAcao(acaoParcial + string);
				sigObjetoCustoHistoricos.setSigAtividades(atividade);
				sigObjetoCustoHistoricosDAO.persistir(sigObjetoCustoHistoricos);
			}
		}
	}

	private void analisaHistoricoGeralAtividade(SigAtividades atividade, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> composicoes, List<SigAtividadePessoas> listaPessoas,
			List<SigAtividadeEquipamentos> listEquipamentoAtividade, List<SigAtividadeInsumos> listAtividadeInsumos,
			List<SigAtividadeServicos> listaServicos, RapServidores rapServidores, List<String> acoes, SigAtividades atividadeClone,
			Map<String, String> componente) {

		StringBuilder acaoGeral = new StringBuilder(73);
		if (!atividade.getNome().equals(atividadeClone.getNome())) {
			acaoGeral.append("nome alterado de " + atividadeClone.getNome() + PARA + atividade.getNome());
		}

		if (atividade.getIndOrigemDados() != atividadeClone.getIndOrigemDados()) {
			if (!acaoGeral.toString().trim().equals("")) {
				acaoGeral.append(", ");
			}
			acaoGeral.append("origem de dados alterada de " + atividadeClone.getIndOrigemDados().getDescricao() + PARA
					+ atividade.getIndOrigemDados().getDescricao());
		}

		if (compararValores(atividade.getSigAtividadeCentroCustos().getFccCentroCustos().getCodigo(), atividadeClone
				.getSigAtividadeCentroCustos().getFccCentroCustos().getCodigo())) {
			if (!acaoGeral.toString().trim().equals("")) {
				acaoGeral.append(", ");
			}
			acaoGeral.append("centro de custo alterado de "
					+ formatarValor(atividadeClone.getSigAtividadeCentroCustos().getFccCentroCustos().getCodigo()) + PARA
					+ formatarValor(atividade.getSigAtividadeCentroCustos().getFccCentroCustos().getCodigo()));
		}

		if (!acaoGeral.toString().trim().equals("")) {
			acoes.add(acaoGeral.toString());
			componente.put(acaoGeral.toString(), "Geral");
		}
	}

	@SuppressWarnings("unchecked")
	private void analisaHistoricoPessoas(SigAtividades atividade, Map<String, Object> clone, List<SigObjetoCustoComposicoes> composicoes,
			List<SigAtividadePessoas> listaPessoas, List<SigAtividadeEquipamentos> listEquipamentoAtividade,
			List<SigAtividadeInsumos> listAtividadeInsumos, List<SigAtividadeServicos> listaServicos, RapServidores rapServidores,
			List<String> acoes, Map<String, String> componente) {

		ArrayList<SigAtividadePessoas> arrayListPessoa = (ArrayList<SigAtividadePessoas>) clone.get(ATIVIDADE_PESSOAS);
		for (SigAtividadePessoas sigAtividadePessoas : listaPessoas) {
			if (sigAtividadePessoas.getSeq() == null) {
				StringBuilder acaoLocal = new StringBuilder("incluído grupo de ocupação ");
				acaoLocal.append(sigAtividadePessoas.getSigGrupoOcupacoes().getDescricao());
				if (sigAtividadePessoas.getQuantidade() != null && sigAtividadePessoas.getQuantidade() != 0) {
					acaoLocal.append(" com " + sigAtividadePessoas.getQuantidade() + " profissionais alocados ");
				}
				if (!sigAtividadePessoas.getTempoMedio().equals("")) {
					acaoLocal.append(sigAtividadePessoas.getTempoMedio());
				}
				acoes.add(acaoLocal.toString());
				componente.put(acaoLocal.toString(), "Pessoas");
			} else {
				SigAtividadePessoas antigo = arrayListPessoa.get(arrayListPessoa.indexOf(sigAtividadePessoas));
				StringBuilder acaoLocal = new StringBuilder(43);
				if (compararValores(sigAtividadePessoas.getQuantidade(), antigo.getQuantidade())) {
					acaoLocal.append(" qtde de profissionais alterado de " + formatarValor(antigo.getQuantidade()) + PARA
							+ formatarValor(sigAtividadePessoas.getQuantidade()));
				}
				if (!sigAtividadePessoas.getTempoMedio().equals(antigo.getTempoMedio())) {
					if (!acaoLocal.toString().trim().equals("")) {
						acaoLocal.append(", ");
					}
					acaoLocal.append(" tempo do profissional alocado alterado de " + formatarValor(antigo.getTempoMedio()) + PARA
							+ formatarValor(sigAtividadePessoas.getTempoMedio()));
				}
				if (!acaoLocal.toString().trim().equals("")) {
					String string = "Alterado grupo de ocupação " + sigAtividadePessoas.getSigGrupoOcupacoes().getDescricao()
							+ acaoLocal.toString() + ".";
					acoes.add(string);
					componente.put(string, "Pessoas");
				}

				if (antigo.getIndSituacao() == DominioSituacao.A && sigAtividadePessoas.getIndSituacao() == DominioSituacao.I) {
					String string = "grupo de ocupação " + sigAtividadePessoas.getSigGrupoOcupacoes().getDescricao() + FOI_DESATIVADO;
					acoes.add(string);
					componente.put(string, "Pessoas");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void analisaHistoricoEquipamentos(SigAtividades atividade, Map<String, Object> clone,
			List<SigObjetoCustoComposicoes> composicoes, List<SigAtividadePessoas> listaPessoas,
			List<SigAtividadeEquipamentos> listEquipamentoAtividade, List<SigAtividadeInsumos> listAtividadeInsumos,
			List<SigAtividadeServicos> listaServicos, RapServidores rapServidores, List<String> acoes, Map<String, String> componente) {

		ArrayList<SigAtividadeEquipamentos> arrayListEquipamento = (ArrayList<SigAtividadeEquipamentos>) clone.get(ATIVIDADE_EQUIPAMENTOS);
		for (SigAtividadeEquipamentos sigAtividadeEquipamentos : listEquipamentoAtividade) {
			if (sigAtividadeEquipamentos.getSeq() == null) {
				StringBuilder acaoLocal = new StringBuilder("incluído equipamento ");
				acaoLocal.append(sigAtividadeEquipamentos.getCodPatrimonio());
				if (sigAtividadeEquipamentos.getSigDirecionadores() != null) {
					acaoLocal.append(" com direcionador ").append(sigAtividadeEquipamentos.getSigDirecionadores().getNome());
				}
				acoes.add(acaoLocal.toString());
				componente.put(acaoLocal.toString(), "Equipamentos");
			} else {
				SigAtividadeEquipamentos antigo = arrayListEquipamento.get(arrayListEquipamento.indexOf(sigAtividadeEquipamentos));
				StringBuilder acaoLocal = new StringBuilder(26);

				if (compararValores(sigAtividadeEquipamentos.getSigDirecionadores(), antigo.getSigDirecionadores())) {

					acaoLocal.append(" direcionador alterado de " + formatarValor(antigo.getSigDirecionadores()) + PARA
							+ formatarValor(sigAtividadeEquipamentos.getSigDirecionadores()));
				}

				if (!acaoLocal.toString().trim().equals("")) {
					String string = "Alterado equipamento " + sigAtividadeEquipamentos.getCodigoCC() + " "
							+ sigAtividadeEquipamentos.getCodPatrimonio() + acaoLocal + ".";
					acoes.add(string);
					componente.put(string, "Equipamentos");
				}

				if (antigo.getIndSituacao() == DominioSituacao.A && sigAtividadeEquipamentos.getIndSituacao() == DominioSituacao.I) {
					String string = "equipamento " + sigAtividadeEquipamentos.getCodigoCC() + " "
							+ sigAtividadeEquipamentos.getCodPatrimonio() + FOI_DESATIVADO;
					acoes.add(string);
					componente.put(string, "Equipamentos");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void analisaHistoricoInsumos(SigAtividades atividade, Map<String, Object> clone, List<SigObjetoCustoComposicoes> composicoes,
			List<SigAtividadePessoas> listaPessoas, List<SigAtividadeEquipamentos> listEquipamentoAtividade,
			List<SigAtividadeInsumos> listAtividadeInsumos, List<SigAtividadeServicos> listaServicos, RapServidores rapServidores,
			List<String> acoes, Map<String, String> componente) {

		ArrayList<SigAtividadeInsumos> arrayListInsumo = (ArrayList<SigAtividadeInsumos>) clone.get(ATIVIDADE_INSUMOS);
		for (SigAtividadeInsumos sigAtividadeInsumos : listAtividadeInsumos) {
			if (sigAtividadeInsumos.getSeq() == null) {
				StringBuilder acaoLocal = new StringBuilder(37);
				acaoLocal.append("incluído material ")
				.append(sigAtividadeInsumos.getMaterial().getCodigoENome());
				if (sigAtividadeInsumos.getQtdeUso() != null) {
					acaoLocal.append(" com quantidade de " + this.formatarValor(sigAtividadeInsumos.getQtdeUso()) + " produtos ");
				}
				if (sigAtividadeInsumos.getVidaUtilQtde() != null) {
					acaoLocal.append(" com vida útil de " + sigAtividadeInsumos.getVidaUtilQtde() + " produtos ");
				}
				if (sigAtividadeInsumos.getVidaUtilTempo() != null) {
					acaoLocal.append(" com vida útil de " + sigAtividadeInsumos.getVidaUtilTempo() + " "
							+ sigAtividadeInsumos.getDirecionadores().getNome());
				}
				acoes.add(acaoLocal.toString());
				componente.put(acaoLocal.toString(), "Insumos");
			} else {
				SigAtividadeInsumos antigo = arrayListInsumo.get(arrayListInsumo.indexOf(sigAtividadeInsumos));
				StringBuilder acaoLocal = new StringBuilder(23);

				if (compararValores(sigAtividadeInsumos.getQtdeUso(), antigo.getQtdeUso())) {
					acaoLocal.append(" qtde de material alterado de " + formatarValor(antigo.getQtdeUso()) + PARA
							+ formatarValor(sigAtividadeInsumos.getQtdeUso()));
				}

				if (compararValores(sigAtividadeInsumos.getVidaUtilQtde(), antigo.getVidaUtilQtde())) {
					acaoLocal.append(" vida útil alterada de qtde " + this.formatarValor(antigo.getVidaUtilQtde()) + " para qtde "
							+ this.formatarValor(sigAtividadeInsumos.getVidaUtilQtde()));
				}
				if (compararValores(sigAtividadeInsumos.getVidaUtilTempo(), antigo.getVidaUtilTempo())) {

					String vidaUtilTempoDirecionadorAntigo = antigo.getVidaUtilTempo() != null ? antigo.getVidaUtilTempo() + " "
							+ antigo.getDirecionadores().getNome() : "nulo";
					String vidaUtilTempoDirecionadorAtual = sigAtividadeInsumos.getVidaUtilTempo() != null ? sigAtividadeInsumos
							.getVidaUtilTempo() + " " + sigAtividadeInsumos.getDirecionadores().getNome() : "nulo";
					acaoLocal.append(" vida útil alterada de " + vidaUtilTempoDirecionadorAntigo + PARA
							+ vidaUtilTempoDirecionadorAtual);
				}

				if (!acaoLocal.toString().trim().equals("")) {
					String string = "Alterado material " + sigAtividadeInsumos.getMaterial().getCodigoENome() + acaoLocal.toString() + ".";
					acoes.add(string);
					componente.put(string, "Insumos");
				}

				if (antigo.getIndSituacao() == DominioSituacao.A && sigAtividadeInsumos.getIndSituacao() == DominioSituacao.I) {
					String string = "material " + sigAtividadeInsumos.getMaterial().getCodigoENome() + FOI_DESATIVADO;
					acoes.add(string);
					componente.put(string, "Insumos");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void analisaHistoricoServicos(SigAtividades atividade, Map<String, Object> clone, List<SigObjetoCustoComposicoes> composicoes,
			List<SigAtividadePessoas> listaPessoas, List<SigAtividadeEquipamentos> listEquipamentoAtividade,
			List<SigAtividadeInsumos> listAtividadeInsumos, List<SigAtividadeServicos> listaServicos, RapServidores rapServidores,
			List<String> acoes, Map<String, String> componente) {

		ArrayList<SigAtividadeServicos> arrayListServico = (ArrayList<SigAtividadeServicos>) clone.get(ATIVIDADE_SERVICOS);
		for (SigAtividadeServicos sigAtividadeServicos : listaServicos) {

			String acao = "";
			boolean comContrato = true;

			if (sigAtividadeServicos.getScoAfContrato() != null) {
				acao = sigAtividadeServicos.getScoAfContrato().getScoContrato().getNrContrato() + " "
						+ sigAtividadeServicos.getScoAfContrato().getScoAutorizacoesForn().getPropostaFornecedor().getId().getLctNumero()
						+ " " + sigAtividadeServicos.getScoAfContrato().getScoAutorizacoesForn().getNroComplemento();
			} else {
				if (sigAtividadeServicos.getScoItensContrato() != null) {
					acao = sigAtividadeServicos.getScoItensContrato().getContrato().getNrContrato() + " "
							+ sigAtividadeServicos.getScoItensContrato().getNrItem();
				} else {
					if (sigAtividadeServicos.getAutorizacaoForn() != null) {
						acao = sigAtividadeServicos.getAutorizacaoForn().getNumero().toString();
						comContrato = false;
					}
				}
			}

			if (sigAtividadeServicos.getSeq() == null) {

				String acaoLocal = "";

				if (comContrato == true) {
					acaoLocal = "incluído contrato de serviço " + acao + COM_DIRECIONADOR_DE
							+ sigAtividadeServicos.getSigDirecionadores().getNome();
				} else {
					acaoLocal = "incluída autorização de fornecimento " + acao + COM_DIRECIONADOR_DE
							+ sigAtividadeServicos.getSigDirecionadores().getNome();
				}

				acoes.add(acaoLocal);
				componente.put(acaoLocal, "Serviços");
			} else {
				SigAtividadeServicos antigo = arrayListServico.get(arrayListServico.indexOf(sigAtividadeServicos));
				StringBuilder acaoLocal = new StringBuilder(26);

				if (compararValores(sigAtividadeServicos.getSigDirecionadores(), antigo.getSigDirecionadores())) {
					acaoLocal.append(" direcionador alterado de " + formatarValor(antigo.getSigDirecionadores().getNome()) + PARA
							+ formatarValor(sigAtividadeServicos.getSigDirecionadores().getNome()));

					String string = "";

					if (comContrato == true) {
						string = "Alterado contrato de serviço " + acao + acaoLocal.toString() + ".";
					} else {
						string = "Alterado autorização de fornecimento " + acao + acaoLocal.toString() + ".";
					}

					acoes.add(string);
					componente.put(string, "Serviços");
				}

				if (antigo.getIndSituacao() == DominioSituacao.A && sigAtividadeServicos.getIndSituacao() == DominioSituacao.I) {

					String string = "";

					if (comContrato == true) {
						string = "contrato de serviço " + acao + FOI_DESATIVADO;
					} else {
						string = "Autorização de fornecimento" + acao + " foi desativada.";
					}

					acoes.add(string);
					componente.put(string, "Serviços");
				}
			}
		}
	}

	private String formatarValor(Object object) {
		if (object != null) {
			if (object instanceof String) {
				if (!((String) object).trim().equals("")) {
					return String.valueOf(object);
				}
			} else if (object instanceof Integer) {
				return String.valueOf(object);
			} else if (object instanceof SigDirecionadores) {
				return ((SigDirecionadores) object).getNome();
			} else if (object instanceof BigDecimal) {
				BigDecimal valor = (BigDecimal) object;
				if (valor.doubleValue() - valor.intValue() > 0) {
					return valor.toString().replace(".", ",");
				} else {
					return String.valueOf(valor.intValue());
				}
			}
		}
		return "nulo";
	}

	private boolean compararValores(Object object1, Object object2) {
		if ((object1 == null && object2 != null) || (object1 != null && object2 == null)) {
			return true;
		} else if (object1 != null && object2 != null) {

			if (object1 instanceof Integer && object2 instanceof Integer) {
				Integer valor1 = (Integer) object1;
				Integer valor2 = (Integer) object2;
				return (valor1.intValue() != valor2.intValue());
			} else if (object1 instanceof BigDecimal && object2 instanceof BigDecimal) {
				BigDecimal valor1 = (BigDecimal) object1;
				BigDecimal valor2 = (BigDecimal) object2;
				return (valor1.intValue() != valor2.intValue());
			} else if (object1 instanceof SigDirecionadores && object2 instanceof SigDirecionadores) {
				SigDirecionadores valor1 = (SigDirecionadores) object1;
				SigDirecionadores valor2 = (SigDirecionadores) object2;
				return (valor1.getSeq().intValue() != valor2.getSeq().intValue());
			}
		}
		return false;
	}

	private SigObjetoCustoHistoricosDAO getSigObjetoCustoHistoricosDAO() {
		return sigObjetoCustoHistoricosDAO;
	}

	protected SigAtividadesDAO getSigAtividadesDAO() {
		return sigAtividadesDAO;
	}

	protected SigObjetoCustoVersoesDAO getSigObjetoCustoVersoesDAO() {
		return sigObjetoCustoVersoesDAO;
	}

	protected SigObjetoCustosDAO getSigObjetoCustosDAO() {
		return sigObjetoCustosDAO;
	}

	protected SigAtividadeServicosDAO getSigAtividadeServicosDAO() {
		return sigAtividadeServicosDAO;
	}

	protected SigAtividadeInsumosDAO getSigAtividadeInsumosDAO() {
		return sigAtividadeInsumosDAO;
	}

	protected SigAtividadePessoasDAO getSigAtividadePessoasDAO() {
		return sigAtividadePessoasDAO;
	}

	protected SigAtividadeEquipamentosDAO getSigAtividadeEquipamentosDAO() {
		return sigAtividadeEquipamentosDAO;
	}
}
