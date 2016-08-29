package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.model.McoGestacoes;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoRecemNascidos;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoGestacoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoRecemNascidosDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

/**
 * Regras de negócio relacionadas à entidade McoLogImpressoes
 * 
 * @author pierre.souza
 * 
 */
@Stateless
public class McoLogImpressoesRN extends BaseBusiness {
	private static final long serialVersionUID = -4849204111108849502L;

	private static final Log LOG = LogFactory.getLog(McoLogImpressoesRN.class);
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;
	
	@Inject 
	private McoRecemNascidosDAO mcoRecemNascidosDAO;
	
	@Inject
	private McoGestacoesDAO mcoGestacoesDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Inject
	private AacConsultasDAO consultasDAO;
	
	
	@Override
	protected Log getLogger() {
		return LOG;
	}

	public void inserirLogImpressoes(Integer gsoPacCodigo, Short gsoSeqp, Integer seqp, 
		Integer conNumero, String evento, Integer matricula, Short vinCodigo, Date criadoEm) {
		
		McoGestacoes mcoGestacoes = mcoGestacoesDAO.pesquisarMcoGestacaoPorId(gsoPacCodigo, gsoSeqp);
		McoRecemNascidos mcoRecemNascidos = mcoRecemNascidosDAO.obterMcoRecemNascidosPorId(gsoPacCodigo, gsoSeqp, seqp);
		
		McoLogImpressoes mcoLogImpressoes = new McoLogImpressoes();
		
		mcoLogImpressoes.setMcoGestacoes(mcoGestacoes);
		mcoLogImpressoes.setRnaSeqp(mcoRecemNascidos != null ? mcoRecemNascidos.getId().getSeqp() : null);	
		
		mcoLogImpressoes.setConsulta(consultasDAO.obterPorChavePrimaria(conNumero));
		mcoLogImpressoes.setEvento(evento);
		mcoLogImpressoes.setCriadoEm(criadoEm);
		
		mcoLogImpressoes.setServidor(servidorDAO.obter(new RapServidoresId(matricula,vinCodigo)));
			
		mcoLogImpressoesDAO.persistir(mcoLogImpressoes);				
	}

}
