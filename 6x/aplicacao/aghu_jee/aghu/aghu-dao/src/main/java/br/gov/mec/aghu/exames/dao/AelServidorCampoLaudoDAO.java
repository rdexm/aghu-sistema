package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.FetchMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelServidorCampoLaudo;
import br.gov.mec.aghu.model.AelServidorCampoLaudoId;
import br.gov.mec.aghu.model.RapServidores;

public class AelServidorCampoLaudoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelServidorCampoLaudo> {
	
	private static final long serialVersionUID = -1042863546024552967L;

	@Override
	protected void obterValorSequencialId(AelServidorCampoLaudo elemento) {
		
		if (elemento == null) {
			throw new IllegalArgumentException("Parametro obrigatorio nao informado!!!");
		}
		
		if (elemento.getCampoLaudo() == null) {
			throw new IllegalArgumentException("Associacao com AelCampoLaudo nao esta corretamente informada!");
		}
		
		if (elemento.getServidor() == null) {
			throw new IllegalArgumentException("Associacao com RapServidores nao esta corretamente informada!");
		}
		
		final Integer serMatricula = elemento.getServidor().getId().getMatricula();
		final Short serVinCodigo = elemento.getServidor().getId().getVinCodigo();
		final Integer calSeq =  elemento.getCampoLaudo().getSeq();
		
		AelServidorCampoLaudoId id = new AelServidorCampoLaudoId();
		id.setSerMatricula(serMatricula);
		id.setSerVinCodigo(serVinCodigo);
		id.setCalSeq(calSeq);
		
		elemento.setId(id);
		
	}
	
	/**
	 * Pesquisa Servidor Campo Laudo por Servidor
	 * @param servidor
	 * @return
	 */
	public List<AelServidorCampoLaudo> pesquisarServidorCampoLaudoPorServidor(RapServidores servidor) {
		return this.pesquisarServidorCampoLaudoPorServidor(servidor.getId().getMatricula(), servidor.getId().getVinCodigo());
	}
	
	/**
	 * Pesquisa Servidor Campo Laudo por Servidor
	 * @param seq
	 * @return
	 */
	public List<AelServidorCampoLaudo> pesquisarServidorCampoLaudoPorServidor(Integer matricula, Short vinCodigo) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AelServidorCampoLaudo.class);
		
		criteria.add(Restrictions.eq(AelServidorCampoLaudo.Fields.SER_MATRICULA.toString(), matricula));
		criteria.add(Restrictions.eq(AelServidorCampoLaudo.Fields.SER_VIN_CODIGO.toString(), vinCodigo));
		
		criteria.setFetchMode(AelServidorCampoLaudo.Fields.CAMPO_LAUDO.toString(), FetchMode.JOIN);
		
		return executeCriteria(criteria);
	}

	/**
	 * Verifica a existência de fluxograma personalizado através do servidor
	 * @param servidor
	 * @return
	 */
	public boolean verificaExitenciaFluxogramaPersonalizado(RapServidores servidor) {
		return this.pesquisarServidorCampoLaudoPorServidor(servidor).isEmpty() ? false : true;
	}

}
