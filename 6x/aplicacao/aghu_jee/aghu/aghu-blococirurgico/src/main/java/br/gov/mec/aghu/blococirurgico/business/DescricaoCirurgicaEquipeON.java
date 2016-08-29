package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfDescricoesDAO;
import br.gov.mec.aghu.blococirurgico.vo.ProfDescricaoCirurgicaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioTipoAtuacao;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoCirurgicaId;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcProfDescricoes;
import br.gov.mec.aghu.model.MbcProfDescricoesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável pelas regras de negócio para cadastro da Equipe - Descrição Cirúrgica. 
 * 
 * @author dpacheco
 *
 */
@Stateless
public class DescricaoCirurgicaEquipeON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(DescricaoCirurgicaEquipeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;

	@Inject
	private MbcProfDescricoesDAO mbcProfDescricoesDAO;

	@Inject
	private MbcDescricaoCirurgicaDAO mbcDescricaoCirurgicaDAO;


	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8363278153989848256L;
	
	/**
	 * Insere instância de MbcProfDescricoes.
	 * 
	 * @param dcgCrgSeq
	 * @param dcgSeqp
	 * @param profDescricaoCirurgicaVO
	 * @throws ApplicationBusinessException 
	 */
	public void inserirProfDescricoes(Integer dcgCrgSeq, Short dcgSeqp, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO
			) throws ApplicationBusinessException {
		// Acha a proxima seqp de mbc_prof_descricoes
		Integer seqp = getMbcProfDescricoesDAO().obterMaiorSeqpProfDescricoesPorDcgCrgSeqEDcgSeqp(dcgCrgSeq, dcgSeqp);
		if (seqp != null) {
			seqp++;
		} else {
			seqp = 1;
		}
		
		MbcProfDescricoes profDescricao = new MbcProfDescricoes();
		profDescricao.setId(new MbcProfDescricoesId(dcgCrgSeq, dcgSeqp, seqp));
		
		// Outros profissionais
		if (DominioTipoAtuacao.OUTR.equals(profDescricaoCirurgicaVO.getTipoAtuacao())) {
			profDescricao.setNome(profDescricaoCirurgicaVO.getNome());
			profDescricao.setCategoria(profDescricaoCirurgicaVO.getCategoria());
		}
		
		profDescricao.setTipoAtuacao(profDescricaoCirurgicaVO.getTipoAtuacao());
		profDescricao.setCriadoEm(null);
		profDescricao.setServidor(servidorLogadoFacade.obterServidorLogado());
		
		RapServidores servidorProf = null; 
		Integer servidorProfMatricula = profDescricaoCirurgicaVO.getSerMatricula();
		Short servidorProfVinCodigo = profDescricaoCirurgicaVO.getSerVinCodigo();
		
		if (servidorProfMatricula != null && servidorProfVinCodigo != null) {
			servidorProf = getRegistroColaboradorFacade().obterRapServidor(
					new RapServidoresId(servidorProfMatricula, servidorProfVinCodigo));			
		}
		
		profDescricao.setServidorProf(servidorProf);
		
		MbcDescricaoCirurgica descricaoCirurgica = getMbcDescricaoCirurgicaDAO().obterPorChavePrimaria(
				new MbcDescricaoCirurgicaId(dcgCrgSeq, dcgSeqp));
		profDescricao.setMbcDescricaoCirurgica(descricaoCirurgica);
		
		getProfDescricoesRN().inserirProfDescricoes(profDescricao);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCP_POPULA_ANEST
	 * 
	 * @param crgSeq
	 * @return
	 */
	public List<MbcProfCirurgias> pesquisarProfCirurgiasAnestesistaPorCrgSeq(Integer crgSeq) {
		return getMbcProfCirurgiasDAO().pesquisarProfCirurgiasAnestesistaPorCrgSeq(crgSeq);
	}
	
	/**
	 * Procedure
	 * 
	 * ORADB: MBCP_POPULA_ENF
	 * 
	 * @param crgSeq
	 * @return
	 */
	public List<MbcProfCirurgias> pesquisarProfCirurgiasEnfermeiroPorCrgSeq(Integer crgSeq) {
		return getMbcProfCirurgiasDAO().listarMbcProfCirurgiasPorFuncao(crgSeq, DominioFuncaoProfissional.ENF);
	}
	
	// Estoria #44927 - correcao para executor de anestesia
	public void persistirProfDescricaoExecutorAnestesia(Integer dcgCrgSeq, Short dcgSeqp, ProfDescricaoCirurgicaVO profDescricaoCirurgicaVO) throws ApplicationBusinessException {
		MbcProfDescricoes profDescricao = mbcProfDescricoesDAO.obterUltimoProfDescricaoPorDescCigExecAnestesia(dcgCrgSeq, dcgSeqp);
		
		if (profDescricao == null && profDescricaoCirurgicaVO != null) {
			profDescricaoCirurgicaVO.setTipoAtuacao(DominioTipoAtuacao.ESE);
			inserirProfDescricoes(dcgCrgSeq, dcgSeqp, profDescricaoCirurgicaVO);
		} else {
			if (profDescricaoCirurgicaVO != null) {
				RapServidoresId id = new RapServidoresId(profDescricaoCirurgicaVO.getSerMatricula(), profDescricaoCirurgicaVO.getSerVinCodigo());
			
				if (!CoreUtil.igual(profDescricao.getServidorProf().getId(), id)) {
					profDescricoesRN.alterarProfDescricoes(profDescricao);
				}
			} else if (profDescricao != null) {
				profDescricoesRN.excluirProfDescricoes(profDescricao);
			}
		}
	}
	
	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}
	
	protected MbcProfDescricoesDAO getMbcProfDescricoesDAO() {
		return mbcProfDescricoesDAO;
	}
	
	protected MbcDescricaoCirurgicaDAO getMbcDescricaoCirurgicaDAO() {
		return mbcDescricaoCirurgicaDAO;
	}
	
	protected ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.iRegistroColaboradorFacade;
	}

}
