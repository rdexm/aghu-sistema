package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnf;
import br.gov.mec.aghu.model.MpmCuidadoUsualUnfId;
import br.gov.mec.aghu.model.RapServidores;


/**
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MPM_CUIDADO_USUAL_UNFS.
 * 
 * @author gmneto
 * 
 */
public class MpmCuidadoUsualUnfDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmCuidadoUsualUnf> {

	private static final long serialVersionUID = -549622474867915720L;
	
	public enum MpmCuidadoUsualUnfDAOExceptionCode implements BusinessExceptionCode {
		MENSAGEM_UNIDADE_FUNCIONAL_EXISTE, MENSAGEM_CUIDADOS_UNIDADE_EXISTE;
	}
	

	/**
	 * lista MpmCuidadoUsualUnf por unidade funcional e cuidado.
	 * 
	 * @param cuidadoUsual
	 * @param unidade
	 * @return
	 */
	public List<MpmCuidadoUsualUnf> listarMpmCuidadoUsualUnfPorUnidadeFuncionalCuidado(
			MpmCuidadoUsual cuidadoUsual, AghUnidadesFuncionais unidade) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);

		criteria.add(Restrictions.eq(
				MpmCuidadoUsualUnf.Fields.CDU_SEQ.toString(), cuidadoUsual));

		criteria.add(Restrictions.eq(
				MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(), unidade));
		return this.executeCriteria(criteria);
	}

	public MpmCuidadoUsualUnf obterPorChavePrimaria(Integer cduSeq, Short unfSeq) {
		MpmCuidadoUsualUnfId id = new MpmCuidadoUsualUnfId();
		id.setCduSeq(cduSeq);
		id.setUnfSeq(unfSeq);
		return this.obterPorChavePrimaria(id);
	}
	
	public List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);
		return executeCriteria(criteria);
	}
	
	public List<MpmCuidadoUsualUnf> listaUnidadeFuncionalPorCuidadoUsual(
			MpmCuidadoUsual cuidado) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);
		criteria.add(Restrictions.eq(
				MpmCuidadoUsualUnf.Fields.CDU_SEQ.toString(), cuidado));
		return executeCriteria(criteria);

	}

	public List<MpmCuidadoUsualUnf> pesquisarCuidadoUsualUnf(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);
		criteria.createAlias(MpmCuidadoUsualUnf.Fields.CDU_SEQ.toString(), "cuidadosUsuais", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional", JoinType.LEFT_OUTER_JOIN);
		if (unidadeFuncional != null) {
			criteria.add(Restrictions.eq(
					MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(),
					unidadeFuncional));
		}
		List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnif = this
				.executeCriteria(criteria, firstResult, maxResult,
						orderProperty, asc);
		return listaMpmCuidadoUsualUnif;
	}

	public Integer pesquisarCuidadoUsualUnfCount(
			AghUnidadesFuncionais unidadeFuncional) {
		return montarConsultaCuidadoUsualUnf(unidadeFuncional);
	}

	private Integer montarConsultaCuidadoUsualUnf(
			AghUnidadesFuncionais unidadeFuncional) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);
		if (unidadeFuncional != null) {
			criteria.add(Restrictions.eq(
					MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(),
					unidadeFuncional));
		}
		List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnif = this
				.executeCriteria(criteria);
		return listaMpmCuidadoUsualUnif.size();
	}

	public void deletarMpmCuidadoUsualUnf(
			MpmCuidadoUsualUnf mpmCuidadoUsualunf, RapServidores servidorLogado)
			throws ApplicationBusinessException {
		remover(mpmCuidadoUsualunf);
		flush();
	}

	public void inserirCuidadosUnf(
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual,
			RapServidores rapServidores) throws ApplicationBusinessException {
		DetachedCriteria criteria2 = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);
		if (unidadeFuncionalPesquisaCuidadoUsual != null) {
			criteria2.add(Restrictions.eq(
					MpmCuidadoUsualUnf.Fields.UNIDADE_FUNCIONAL.toString(),
					unidadeFuncionalPesquisaCuidadoUsual));
		}
		List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnif = this
				.executeCriteria(criteria2);
		DetachedCriteria criteria0 = DetachedCriteria
				.forClass(MpmCuidadoUsual.class);
		List<MpmCuidadoUsual> listaMpmCuidadoUsual = this
				.executeCriteria(criteria0);
		if (listaMpmCuidadoUsualUnif.size() != listaMpmCuidadoUsual.size()) {
			for (MpmCuidadoUsual mpmCuidadoUsual : listaMpmCuidadoUsual) {
				MpmCuidadoUsualUnf mpmCuidadoUsualTmp = new MpmCuidadoUsualUnf();
				MpmCuidadoUsualUnfId MpmCuidadoUsualUnfIdTmp = new MpmCuidadoUsualUnfId();
				if (incluirUnf(mpmCuidadoUsual,
						unidadeFuncionalPesquisaCuidadoUsual,
						listaMpmCuidadoUsualUnif)) {
					MpmCuidadoUsualUnfIdTmp.setCduSeq(mpmCuidadoUsual.getSeq());
					MpmCuidadoUsualUnfIdTmp
							.setUnfSeq(unidadeFuncionalPesquisaCuidadoUsual
									.getSeq());
					mpmCuidadoUsualTmp.setId(MpmCuidadoUsualUnfIdTmp);
					mpmCuidadoUsualTmp.setMpmCuidadoUsuais(mpmCuidadoUsual);
					mpmCuidadoUsualTmp.setRapServidores(rapServidores);
					mpmCuidadoUsualTmp
							.setUnidadeFuncional(unidadeFuncionalPesquisaCuidadoUsual);
					mpmCuidadoUsualTmp.setCriadoEm(new Date());
					persistir(mpmCuidadoUsualTmp);
					flush();
				}
			}
		} else {
			throw new ApplicationBusinessException(
					MpmCuidadoUsualUnfDAOExceptionCode.MENSAGEM_UNIDADE_FUNCIONAL_EXISTE);
		}
	}

	private boolean incluirUnf(MpmCuidadoUsual mpmCuidadoUsual,
			AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual,
			List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnif) {
		for (MpmCuidadoUsualUnf mpmCuidadoUsualUnf : listaMpmCuidadoUsualUnif) {
			if (mpmCuidadoUsualUnf.getMpmCuidadoUsuais()
					.equals(mpmCuidadoUsual)
					&& mpmCuidadoUsualUnf.getUnidadeFuncional().equals(
							unidadeFuncionalPesquisaCuidadoUsual)) {
				return false;
			}
		}
		return true;
	}

	public void inserirTodosCuidadosUnf(RapServidores rapServidores)
			throws ApplicationBusinessException {
		DetachedCriteria criteria0 = DetachedCriteria
				.forClass(MpmCuidadoUsual.class);
		List<MpmCuidadoUsual> listaMpmCuidadoUsual = this
				.executeCriteria(criteria0);
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AghUnidadesFuncionais.class);
		List<AghUnidadesFuncionais> listaAghUnidadesFuncionais = this
				.executeCriteria(criteria);
		DetachedCriteria criteria2 = DetachedCriteria
				.forClass(MpmCuidadoUsualUnf.class);
		List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnif = this
				.executeCriteria(criteria2);
		List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnifTmp = new ArrayList<MpmCuidadoUsualUnf>();
		for (MpmCuidadoUsual mpmCuidadoUsual : listaMpmCuidadoUsual) {
			for (AghUnidadesFuncionais aghUnidadesFuncionais : listaAghUnidadesFuncionais) {
				MpmCuidadoUsualUnf mpmCuidadoUsualUnf = new MpmCuidadoUsualUnf();
				if (excluirLinha(mpmCuidadoUsual, aghUnidadesFuncionais,
						listaMpmCuidadoUsualUnif)) {
					MpmCuidadoUsualUnfId MpmCuidadoUsualUnfIdTmp = new MpmCuidadoUsualUnfId();
					MpmCuidadoUsualUnfIdTmp.setCduSeq(mpmCuidadoUsual.getSeq());
					MpmCuidadoUsualUnfIdTmp.setUnfSeq(aghUnidadesFuncionais
							.getSeq());
					mpmCuidadoUsualUnf.setId(MpmCuidadoUsualUnfIdTmp);
					mpmCuidadoUsualUnf.setMpmCuidadoUsuais(mpmCuidadoUsual);
					mpmCuidadoUsualUnf
							.setUnidadeFuncional(aghUnidadesFuncionais);
					mpmCuidadoUsualUnf.setRapServidores(rapServidores);
					mpmCuidadoUsualUnf.setCriadoEm(new Date());
					listaMpmCuidadoUsualUnifTmp.add(mpmCuidadoUsualUnf);
				}
			}
		}
		if (listaMpmCuidadoUsualUnifTmp.size() != 0) {
			for (MpmCuidadoUsualUnf mpmCuidadoUsualUnf : listaMpmCuidadoUsualUnifTmp) {
				persistir(mpmCuidadoUsualUnf);
				flush();
			}
		} else {
			throw new ApplicationBusinessException(
					MpmCuidadoUsualUnfDAOExceptionCode.MENSAGEM_CUIDADOS_UNIDADE_EXISTE);
		}
	}
	
	private boolean excluirLinha(MpmCuidadoUsual mpmCuidadoUsual,
			AghUnidadesFuncionais aghUnidadesFuncionais,
			List<MpmCuidadoUsualUnf> listaMpmCuidadoUsualUnif) {
		for (MpmCuidadoUsualUnf MpmCuidadoUsualUnfTmp : listaMpmCuidadoUsualUnif) {
			if (MpmCuidadoUsualUnfTmp.getMpmCuidadoUsuais().equals(
					mpmCuidadoUsual)
					&& MpmCuidadoUsualUnfTmp.getUnidadeFuncional().equals(
							aghUnidadesFuncionais)) {
				return false;
			}
		}
		return true;
	}

}