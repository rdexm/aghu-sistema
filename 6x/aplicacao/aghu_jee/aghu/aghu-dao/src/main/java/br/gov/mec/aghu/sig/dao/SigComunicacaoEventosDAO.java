package br.gov.mec.aghu.sig.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoEventoComunicacao;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SigComunicacaoEventos;

public class SigComunicacaoEventosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigComunicacaoEventos> {

	private static final long serialVersionUID = -5581802121089952949L;

	public List<RapServidores> listarUsuariosNotificaveis(DominioTipoEventoComunicacao tipoEvento, Integer codigoCentroCusto) {
		List<RapServidores> servidores = new ArrayList<RapServidores>();
		// Buscar todos os usuários configurados no módulo de custos
		List<SigComunicacaoEventos> usuariosEspecificos = this.buscarUsuarioEspecifico(tipoEvento, codigoCentroCusto);
		for (SigComunicacaoEventos comunicacaoEventos : usuariosEspecificos) {
			servidores.add(comunicacaoEventos.getServidor());
		}

		// Quando o evento tem relação com o centro de custo, deve executar o
		// SQL busca usuário responsável do centro de custo (chefia)
		if (DominioTipoEventoComunicacao.CE.equals(tipoEvento) && codigoCentroCusto != null) {
			servidores.add(this.buscarUsuarioResponsavelCentroCusto(codigoCentroCusto));
		}

		// Identifica que não existe nenhum usuário a ser comunicado sobre a
		// ocorrência do evento
		if (servidores.isEmpty()) {
			// Envia a mensagem à central de pendências e um email para o
			// usuário administrador geral
			List<SigComunicacaoEventos> usuariosGenericos = this.buscarUsuarioGenerico();
			for (SigComunicacaoEventos comunicacaoEventos : usuariosGenericos) {
				servidores.add(comunicacaoEventos.getServidor());
			}
		}
		return servidores;
	}

	private List<SigComunicacaoEventos> buscarUsuarioEspecifico(DominioTipoEventoComunicacao tipoEvento, Integer codigoCentroCusto) {
		DetachedCriteria criteria = this.criarCriteriaBuscaUsuario();
		criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.TIPO_EVENTO.toString(), tipoEvento));
		if (tipoEvento.equals(DominioTipoEventoComunicacao.CE) && codigoCentroCusto != null) {
			criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.CENTRO_CUSTO.toString() + "." + FccCentroCustos.Fields.CODIGO.toString(),
					codigoCentroCusto));
		} else {
			criteria.add(Restrictions.isNull(SigComunicacaoEventos.Fields.CENTRO_CUSTO.toString()));
		}
		return this.executeCriteria(criteria);
	}

	private List<SigComunicacaoEventos> buscarUsuarioGenerico() {
		DetachedCriteria criteria = this.criarCriteriaBuscaUsuario();
		criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.TIPO_EVENTO.toString(), DominioTipoEventoComunicacao.AG));
		return this.executeCriteria(criteria);
	}

	public RapServidores buscarUsuarioResponsavelCentroCusto(Integer codigoCentroCusto) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);
		criteria.add(Restrictions.eq(FccCentroCustos.Fields.CODIGO.toString(), codigoCentroCusto));
		FccCentroCustos centroCustos = (FccCentroCustos) this.executeCriteriaUniqueResult(criteria);
		return centroCustos.getRapServidor();
	}

	private DetachedCriteria criarCriteriaBuscaUsuario() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigComunicacaoEventos.class);
		criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}

	public List<SigComunicacaoEventos> pesquisarComunicacaoEventos(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, SigComunicacaoEventos sigComunicacaoEventos) {
		DetachedCriteria criteria = criarCriteria(sigComunicacaoEventos);
		return executeCriteriaOrdenada(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long pesquisarComunicacaoEventosCount(SigComunicacaoEventos sigComunicacaoEventos) {
		DetachedCriteria criteria = criarCriteria(sigComunicacaoEventos);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criarCriteria(SigComunicacaoEventos sigComunicacaoEventos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigComunicacaoEventos.class);

		criteria.createAlias(SigComunicacaoEventos.Fields.SERVIDOR.toString(), "serv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("serv."+RapServidores.Fields.PESSOA_FISICA.toString(), "pes", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(SigComunicacaoEventos.Fields.CENTRO_CUSTO.toString(), "cent", JoinType.LEFT_OUTER_JOIN);
		
		
		if (sigComunicacaoEventos.getTipoEvento() != null) {
			criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.TIPO_EVENTO.toString(), sigComunicacaoEventos.getTipoEvento()));
		}

		if (sigComunicacaoEventos.getFccCentroCustos() != null) {
			criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.CENTRO_CUSTO.toString(), sigComunicacaoEventos.getFccCentroCustos()));
		}

		if (sigComunicacaoEventos.getSituacao() != null) {
			criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.SITUACAO.toString(), sigComunicacaoEventos.getSituacao()));
		}

		if (sigComunicacaoEventos.getServidor() != null) {
			criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.SERVIDOR.toString(), sigComunicacaoEventos.getServidor()));
		}

		return criteria;
	}

	private List<SigComunicacaoEventos> executeCriteriaOrdenada(DetachedCriteria criteria, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		criteria.addOrder(Order.asc(SigComunicacaoEventos.Fields.TIPO_EVENTO.toString()));
		criteria.addOrder(Order.asc(SigComunicacaoEventos.Fields.CENTRO_CUSTO.toString()));
		criteria.addOrder(Order.asc(SigComunicacaoEventos.Fields.SERVIDOR.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public List<SigComunicacaoEventos> buscaDuplicidadeComunicacaoEvento(SigComunicacaoEventos sigComunicacaoEventos) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigComunicacaoEventos.class);
		criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.TIPO_EVENTO.toString(), sigComunicacaoEventos.getTipoEvento()));

		if (sigComunicacaoEventos.getFccCentroCustos() != null) {
			criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.CENTRO_CUSTO.toString(), sigComunicacaoEventos.getFccCentroCustos()));
		}

		criteria.add(Restrictions.eq(SigComunicacaoEventos.Fields.SERVIDOR.toString(), sigComunicacaoEventos.getServidor()));

		// alteracao
		if (sigComunicacaoEventos.getSeq() != null) {
			criteria.add(Restrictions.ne(SigComunicacaoEventos.Fields.SEQ.toString(), sigComunicacaoEventos.getSeq()));
		}

		return executeCriteria(criteria);
	}

}
