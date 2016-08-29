package br.gov.mec.aghu.sig.custos.business;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Hibernate;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoVersoesCustos;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigAtividadeCentroCustos;
import br.gov.mec.aghu.model.SigAtividadeEquipamentos;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.model.SigAtividadePessoas;
import br.gov.mec.aghu.model.SigAtividadeServicos;
import br.gov.mec.aghu.model.SigAtividades;
import br.gov.mec.aghu.model.SigObjetoCustoComposicoes;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.sig.dao.SigAtividadeCentroCustosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeEquipamentosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeInsumosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadePessoasDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadeServicosDAO;
import br.gov.mec.aghu.sig.dao.SigAtividadesDAO;
import br.gov.mec.aghu.sig.dao.SigObjetoCustoComposicoesDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterAtividadesON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ManterAtividadesON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SigAtividadeServicosDAO sigAtividadeServicosDAO;
	
	@Inject
	private SigAtividadeInsumosDAO sigAtividadeInsumosDAO;
	
	@Inject
	private SigAtividadesDAO sigAtividadesDAO;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SigObjetoCustoComposicoesDAO sigObjetoCustoComposicoesDAO;
	
	@Inject
	private SigAtividadePessoasDAO sigAtividadePessoasDAO;
	
	@Inject
	private SigAtividadeCentroCustosDAO sigAtividadeCentroCustosDAO;
	
	@Inject
	private SigAtividadeEquipamentosDAO sigAtividadeEquipamentosDAO;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;

	private static final long serialVersionUID = -8884311081504709177L;

	public enum ManterAtividadesONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_VINCULO_ATIVIDADE;
	}

	public void excluirAtividade(SigAtividades atividade) throws ApplicationBusinessException {
		if (verificaAtividadeEstaVinculadaAObjetoCusto(atividade)) {
			throw new ApplicationBusinessException(ManterAtividadesONExceptionCode.MENSAGEM_VINCULO_ATIVIDADE);
		} else {
			this.getSigAtividadesDAO().removerPorId(atividade.getSeq());
		}
	}

	public boolean verificaAtividadeEstaVinculadaAObjetoCusto(SigAtividades atividade) {
		List<SigObjetoCustoComposicoes> lista = this.getSigObjetoCustoComposicoesDAO().buscarPorAtividade(atividade);
		boolean estaVinculadaObjetoCusto = false;
		if (lista != null) {
			for (SigObjetoCustoComposicoes composicao : lista) {
				//#24615 - Deve ser permitido exluir itens da composição de uma atividade quando a situação do objeto de custo ao qual ela está associada for "Em elaboração".
				if (!composicao.getSigObjetoCustoVersoes().getIndSituacao().equals(DominioSituacaoVersoesCustos.E)) {
					estaVinculadaObjetoCusto = true;
					break;
				}
			}
		}
		return estaVinculadaObjetoCusto;
	}

	public void persistirAtividade(SigAtividades sigAtividades) {
		// alteração
		if (sigAtividades.getSeq() != null) {
			this.getSigAtividadesDAO().atualizar(sigAtividades);
			// inclusão
		} else {
			this.getSigAtividadesDAO().persistir(sigAtividades);
		}
	}

	public SigAtividades copiaAtividade(SigAtividades atividadesSuggestion, String nome, boolean pessoal, boolean insumos, boolean equipamentos,
			boolean servicos) throws ApplicationBusinessException {
		atividadesSuggestion = this.getSigAtividadesDAO().merge(atividadesSuggestion);
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();	
		SigAtividades novaAtividade = new SigAtividades();
		novaAtividade.setNome(nome);
		novaAtividade.setCriadoEm(new Date());
		novaAtividade.setVersion(0);
		novaAtividade.setIndSituacao(DominioSituacao.A);
		novaAtividade.setIndOrigemDados(atividadesSuggestion.getIndOrigemDados());
		novaAtividade.setRapServidores(servidorLogado);

		SigAtividadeCentroCustos novaAtividadeCentroCusto = new SigAtividadeCentroCustos();
		novaAtividadeCentroCusto.setVersion(0);
		novaAtividadeCentroCusto.setCriadoEm(new Date());
		novaAtividadeCentroCusto.setIndSituacao(DominioSituacao.A);
		novaAtividadeCentroCusto.setRapServidores(servidorLogado);
		novaAtividadeCentroCusto.setSigAtividades(novaAtividade);
		novaAtividadeCentroCusto.setFccCentroCustos(atividadesSuggestion.getSigAtividadeCentroCustos().getFccCentroCustos());

		Set<SigAtividadeCentroCustos> listSigAtividadeCentroCustos = new HashSet<SigAtividadeCentroCustos>();
		listSigAtividadeCentroCustos.add(novaAtividadeCentroCusto);
		novaAtividade.setListSigAtividadeCentroCustos(listSigAtividadeCentroCustos);

		this.getSigAtividadesDAO().persistir(novaAtividade);

		if (pessoal) {
			SigAtividadePessoasDAO sigAtividadePessoasDAO = this.getSigAtividadePessoasDAO();
			for (SigAtividadePessoas sigAtividadePessoas : atividadesSuggestion.getListAtividadePessoas()) {
				SigAtividadePessoas novaAtividadePessoa = new SigAtividadePessoas();
				novaAtividadePessoa.setIndSituacao(DominioSituacao.A);
				novaAtividadePessoa.setCriadoEm(new Date());
				novaAtividadePessoa.setRapServidores(servidorLogado);
				novaAtividadePessoa.setVersion(0);
				novaAtividadePessoa.setQuantidade(sigAtividadePessoas.getQuantidade());
				novaAtividadePessoa.setTempo(sigAtividadePessoas.getTempo());
				novaAtividadePessoa.setSigDirecionadores(sigAtividadePessoas.getSigDirecionadores());
				novaAtividadePessoa.setSigGrupoOcupacoes(sigAtividadePessoas.getSigGrupoOcupacoes());
				novaAtividadePessoa.setSigAtividades(novaAtividade);
				sigAtividadePessoasDAO.persistir(novaAtividadePessoa);
			}
		}

		if (insumos) {
			SigAtividadeInsumosDAO sigAtividadeInsumosDAO = this.getSigAtividadeInsumosDAO();
			for (SigAtividadeInsumos sigAtividadeInsumo : atividadesSuggestion.getListAtividadeInsumos()) {
				SigAtividadeInsumos novaAtividadeInsumo = new SigAtividadeInsumos();
				novaAtividadeInsumo.setIndSituacao(DominioSituacao.A);
				novaAtividadeInsumo.setCriadoEm(new Date());
				novaAtividadeInsumo.setRapServidores(servidorLogado);
				novaAtividadeInsumo.setVersion(0);
				novaAtividadeInsumo.setQtdeUso(sigAtividadeInsumo.getQtdeUso());
				novaAtividadeInsumo.setUnidadeMedida(sigAtividadeInsumo.getUnidadeMedida());
				novaAtividadeInsumo.setDirecionadores(sigAtividadeInsumo.getDirecionadores());
				novaAtividadeInsumo.setVidaUtilQtde(sigAtividadeInsumo.getVidaUtilQtde());
				novaAtividadeInsumo.setVidaUtilTempo(sigAtividadeInsumo.getVidaUtilTempo());
				novaAtividadeInsumo.setMaterial(sigAtividadeInsumo.getMaterial());
				novaAtividadeInsumo.setSigAtividades(novaAtividade);
				sigAtividadeInsumosDAO.persistir(novaAtividadeInsumo);
			}
		}

		if (equipamentos) {
			SigAtividadeEquipamentosDAO sigAtividadeEquipamentosDAO = this.getSigAtividadeEquipamentosDAO();
			for (SigAtividadeEquipamentos sigAtividadeEquipamentos : atividadesSuggestion.getListAtividadeEquipamentos()) {
				SigAtividadeEquipamentos novaAtividadeEquipamentos = new SigAtividadeEquipamentos();
				novaAtividadeEquipamentos.setIndSituacao(DominioSituacao.A);
				novaAtividadeEquipamentos.setCriadoEm(new Date());
				novaAtividadeEquipamentos.setServidorResp(servidorLogado);
				novaAtividadeEquipamentos.setVersion(0);
				novaAtividadeEquipamentos.setSigDirecionadores(sigAtividadeEquipamentos.getSigDirecionadores());
				novaAtividadeEquipamentos.setCodPatrimonio(sigAtividadeEquipamentos.getCodPatrimonio());
				novaAtividadeEquipamentos.setSigAtividades(novaAtividade);
				sigAtividadeEquipamentosDAO.persistir(novaAtividadeEquipamentos);
			}
		}

		if (servicos) {
			SigAtividadeServicosDAO sigAtividadeServicosDAO = this.getSigAtividadeServicosDAO();
			for (SigAtividadeServicos sigAtividadeServicos : atividadesSuggestion.getListAtividadeServicos()) {
				SigAtividadeServicos novaAtividadeServicos = new SigAtividadeServicos();
				novaAtividadeServicos.setIndSituacao(DominioSituacao.A);
				novaAtividadeServicos.setCriadoEm(new Date());
				novaAtividadeServicos.setRapServidores(servidorLogado);
				novaAtividadeServicos.setVersion(0);
				novaAtividadeServicos.setSigDirecionadores(sigAtividadeServicos.getSigDirecionadores());
				novaAtividadeServicos.setScoAfContrato(sigAtividadeServicos.getScoAfContrato());
				novaAtividadeServicos.setScoItensContrato(sigAtividadeServicos.getScoItensContrato());
				novaAtividadeServicos.setSigAtividades(novaAtividade);
				sigAtividadeServicosDAO.persistir(novaAtividadeServicos);
			}
		}
		this.getSigAtividadesDAO().flush();
		return this.getSigAtividadesDAO().merge(novaAtividade);
	}

	public void removerAtividadeCentroCustos(List<SigAtividadeCentroCustos> lista) {
		if (lista != null) {
			for (SigAtividadeCentroCustos sigAtividadeCentroCustos : lista) {
				this.getSigAtividadeCentroCustosDAO().removerPorId(sigAtividadeCentroCustos.getSeq());
			}
			lista = null;
		}
	}

	public boolean possuiCalculo(SigAtividades atividade) {
		atividade  = this.getSigAtividadesDAO().merge(atividade);
		if (atividade == null) {
			return false;
		}
		if (atividade.getListAtividadeEquipamentos() != null && !atividade.getListAtividadeEquipamentos().isEmpty()) {
			return true;
		}
		if (atividade.getListAtividadeInsumos() != null && !atividade.getListAtividadeInsumos().isEmpty()) {
			return true;
		}
		if (atividade.getListAtividadePessoas() != null && !atividade.getListAtividadePessoas().isEmpty()) {
			return true;
		}
		if (atividade.getListAtividadeServicos() != null && !atividade.getListAtividadeServicos().isEmpty()) {
			return true;
		}
		return false;

	}

	// FACADES
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected ICentroCustoFacade getCentroCustoFacade() {
		return centroCustoFacade;
	}

	// DAOs
	protected SigAtividadesDAO getSigAtividadesDAO() {
		return sigAtividadesDAO;
	}

	protected SigAtividadePessoasDAO getSigAtividadePessoasDAO() {
		return sigAtividadePessoasDAO;
	}

	protected SigAtividadeEquipamentosDAO getSigAtividadeEquipamentosDAO() {
		return sigAtividadeEquipamentosDAO;
	}

	protected SigAtividadeServicosDAO getSigAtividadeServicosDAO() {
		return sigAtividadeServicosDAO;
	}

	protected SigAtividadeInsumosDAO getSigAtividadeInsumosDAO() {
		return sigAtividadeInsumosDAO;
	}

	protected SigObjetoCustoComposicoesDAO getSigObjetoCustoComposicoesDAO() {
		return sigObjetoCustoComposicoesDAO;
	}

	protected SigAtividadeCentroCustosDAO getSigAtividadeCentroCustosDAO() {
		return sigAtividadeCentroCustosDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	public List<SigAtividadeInsumos> pesquisarAtividadeInsumos(
			Integer seqAtividade) {
		List<SigAtividadeInsumos> lista = this.getSigAtividadeInsumosDAO().pesquisarAtividadeInsumos(seqAtividade);
		for (SigAtividadeInsumos sigAtividadeInsumos : lista) {
			if(sigAtividadeInsumos.getMaterial() != null ){
				Hibernate.initialize(sigAtividadeInsumos.getMaterial().getEstoquesGerais());
			}
		}
		return lista;
	}
	
}