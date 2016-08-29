package br.gov.mec.aghu.blococirurgico.procdiagterap.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.PdtDescricao;
import br.gov.mec.aghu.model.PdtProf;
import br.gov.mec.aghu.model.PdtProfId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class DescricaoProcDiagTerapEquipeON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(DescricaoProcDiagTerapEquipeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	


	@EJB
	private IBlocoCirurgicoProcDiagTerapFacade iBlocoCirurgicoProcDiagTerapFacade;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	@EJB
	private PdtProfRN pdtProfRN;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8989961959121575821L;

	/**
	 * Insere instância de PdtProf.
	 * 
	 * @param ddtSeq
	 * @param profDescricaoCirurgicaVO
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirProf(Integer ddtSeq, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO
			) throws ApplicationBusinessException {
		
		Short dpfSeqp = null;
		
		IBlocoCirurgicoProcDiagTerapFacade blocoCirurgicoProcDiagTerapFacade = getBlocoCirurgicoProcDiagTerapFacade();
		
		// Acha a proxima seqp de pdt_profs
		Short dpfSeqpAtual = blocoCirurgicoProcDiagTerapFacade.obterMaiorSeqpProfPorDdtSeq(ddtSeq);
		if (dpfSeqpAtual != null) {
			dpfSeqp = (short) (dpfSeqpAtual + 1);
		} else {
			dpfSeqp = 1;	
		}
		
		PdtProf prof = new PdtProf();
		prof.setId(new PdtProfId(ddtSeq, dpfSeqp));
		
		// Outros profissionais
		if (DominioTipoAtuacao.OUTR.equals(profDescricaoCirurgicaVO.getTipoAtuacao())) {
			prof.setNome(profDescricaoCirurgicaVO.getNome());
			prof.setCategoria(profDescricaoCirurgicaVO.getCategoriaPdt());
		}
		
		prof.setTipoAtuacao(profDescricaoCirurgicaVO.getTipoAtuacao());
		prof.setCriadoEm(null);
		prof.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		RapServidores servidorPrf = null; 
		Integer servidorProfMatricula = profDescricaoCirurgicaVO.getSerMatricula();
		Short servidorProfVinCodigo = profDescricaoCirurgicaVO.getSerVinCodigo();
		
		if (servidorProfMatricula != null && servidorProfVinCodigo != null) {
			servidorPrf = getRegistroColaboradorFacade().obterRapServidor(
					new RapServidoresId(servidorProfMatricula, servidorProfVinCodigo));			
		}
		
		prof.setServidorPrf(servidorPrf);
		
		Enum[] fetchArgsInnerJoin = { PdtDescricao.Fields.SERVIDOR};
		
		PdtDescricao descricao = blocoCirurgicoProcDiagTerapFacade.obterPdtDescricao(ddtSeq, fetchArgsInnerJoin, null);
		prof.setPdtDescricao(descricao);
		
		getPdtProfRN().inserirProf(prof);
	}
	
	
	protected PdtProfRN getPdtProfRN() {
		return pdtProfRN;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}
	
	protected IBlocoCirurgicoProcDiagTerapFacade getBlocoCirurgicoProcDiagTerapFacade() {
		return this.iBlocoCirurgicoProcDiagTerapFacade;
	}

}
