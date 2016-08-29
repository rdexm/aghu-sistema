package br.gov.mec.aghu.controleinfeccao.business;

import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.controleinfeccao.dao.MciMicroorgPatologiaExameDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMicroorgPatologiaExameJnDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMicroorganismoPatologiaDAO;
import br.gov.mec.aghu.controleinfeccao.dao.MciMicroorganismoPatologiaJnDAO;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.ResultadoCodificadoExameVO;
import br.gov.mec.aghu.model.AelResultadoCodificado;
import br.gov.mec.aghu.model.AelResultadoCodificadoId;
import br.gov.mec.aghu.model.MciMicroorgPatologiaExame;
import br.gov.mec.aghu.model.MciMicroorgPatologiaExameId;
import br.gov.mec.aghu.model.MciMicroorgPatologiaExameJn;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;
import br.gov.mec.aghu.model.MciMicroorganismoPatologiaId;
import br.gov.mec.aghu.model.MciMicroorganismoPatologiaJn;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class MciMicroorganismoPatologiaRN  extends BaseBusiness {

	private enum MciMicroorganismoPatologiaRNException implements
	BusinessExceptionCode {
		AMP_MENSAGEM_RESTRICAO_ALTERACAO, AMP_MENSAGEM_DADOS_INCOMPLETOS, AMP_MENSAGEM_PERIODO, AMP_MENSAGEM_REGISTRO_DUPLICADO;

	}

	private static final long serialVersionUID = -7357366435896254557L;

	private static final Log LOG = LogFactory.getLog(MciMicroorganismoPatologiaRN.class);

	@Inject
	private MciMicroorgPatologiaExameDAO mciMicroorgPatologiaExameDAO;

	@Inject
	private MciMicroorganismoPatologiaDAO mciMicroorganismoPatologiaDAO;

	@Inject
	private MciMicroorgPatologiaExameJnDAO mciMicroorgPatologiaExameJnDAO;

	@Inject
	private MciMicroorganismoPatologiaJnDAO mciMicroorganismoPatologiaJnDAO;


	@EJB
	private IParametroFacade parametroFacade;
	@EJB
	private ICadastrosApoioExamesFacade cadastroExamesFacade;


	// RN3
	public void atualizarMciMicroorganismoPatologia(MciMicroorganismoPatologia entidade, RapServidores usuario) throws ApplicationBusinessException {
		if(StringUtils.isBlank(entidade.getDescricao())) {
			throw new  ApplicationBusinessException(MciMicroorganismoPatologiaRNException.AMP_MENSAGEM_DADOS_INCOMPLETOS);
		}
		if(isAlterandoDescricao(entidade)) {
			throw new  ApplicationBusinessException(MciMicroorganismoPatologiaRNException.AMP_MENSAGEM_RESTRICAO_ALTERACAO);
		}

		// TODO ALTERACAO
		entidade.setAlteradoEm(new Date());
		entidade.setRapServidoresMov(usuario);
		mciMicroorganismoPatologiaJnDAO.persistir(criarMciMicroorganismoPatologiaJn(DominioOperacoesJournal.UPD, entidade, usuario));
		mciMicroorganismoPatologiaDAO.merge(entidade);
	}



	// RN6
	public void atualizarMciMicroorganismoPatologiaExame(
			ResultadoCodificadoExameVO entidade, RapServidores usuario)
					throws ApplicationBusinessException {
		MciMicroorgPatologiaExameId id = new MciMicroorgPatologiaExameId(entidade.getSeqPai(), entidade.getSeqp(), entidade.getRcdSeq(), entidade.getGtcSeq());
		MciMicroorgPatologiaExame exame = mciMicroorgPatologiaExameDAO
				.obterPorChavePrimaria(id);
		exame.setId(id);
		exame.setRapServidoresMov(usuario);
		exame.setAlteradoEm(new Date());
		exame.setIndSituacao(entidade.getSituacaoExame());
		mciMicroorgPatologiaExameJnDAO
		.persistir(criarMciMicroorgPatologiaExameJn(
				DominioOperacoesJournal.UPD, exame, usuario));
		mciMicroorgPatologiaExameDAO.merge(exame);
	}



	//	RN1
	public void deletarMicroorganismoPatologia(MciMicroorganismoPatologia entidade, RapServidores usuario) throws ApplicationBusinessException {
		MciMicroorganismoPatologia original = mciMicroorganismoPatologiaDAO.obterPorChavePrimaria(entidade.getId());
		if(isDentroPrazoExclusao(original.getCriadoEm())) {

			mciMicroorganismoPatologiaJnDAO.persistir(criarMciMicroorganismoPatologiaJn(DominioOperacoesJournal.DEL, original, usuario));
			deletarExames(original.getMciMicroorgPatologiaExamees(), usuario);
			mciMicroorganismoPatologiaDAO.remover(original);
		} else {
			throw new  ApplicationBusinessException(MciMicroorganismoPatologiaRNException.AMP_MENSAGEM_PERIODO);
		}
	}

	public void deletarMicroorganismoPatologiaExame(
			MciMicroorgPatologiaExame exame, RapServidores usuario)
					throws ApplicationBusinessException {

		if(isDentroPrazoExclusao(exame.getCriadoEm())) {

			mciMicroorgPatologiaExameJnDAO.persistir(criarMciMicroorgPatologiaExameJn(DominioOperacoesJournal.DEL, exame, usuario));
			deletarMciPatologiaExamePorId(exame.getId());
		} else {
			throw new  ApplicationBusinessException(MciMicroorganismoPatologiaRNException.AMP_MENSAGEM_PERIODO);
		}
	}


	//RN4
	public void deletarMicroorganismoPatologiaExame(
			ResultadoCodificadoExameVO entidade, RapServidores usuario)
					throws ApplicationBusinessException {
		MciMicroorgPatologiaExameId id = new MciMicroorgPatologiaExameId(entidade.getSeqPai(), entidade.getSeqp(), entidade.getRcdSeq(), entidade.getGtcSeq());
		MciMicroorgPatologiaExame exame = mciMicroorgPatologiaExameDAO. obterPorChavePrimaria(id);
		exame.setId(id);
		if(isDentroPrazoExclusao(exame.getCriadoEm())) {
			mciMicroorgPatologiaExameJnDAO.persistir(criarMciMicroorgPatologiaExameJn(DominioOperacoesJournal.DEL, exame, usuario));
			deletarMciPatologiaExamePorId(exame.getId());
		} else {
			throw new  ApplicationBusinessException(MciMicroorganismoPatologiaRNException.AMP_MENSAGEM_PERIODO);
		}
	}


	@Override
	public Log getLogger() {
		return LOG;
	}

	//Exibe mensagem MENSAGEM_Sucesso_CAdastro
	//RN2
	public void inserirMciMicroorganismoPatologia(
			MciPatologiaInfeccao mciPatologiaInfeccao,
			MciMicroorganismoPatologia entidade, RapServidores usuario) {
		MciMicroorganismoPatologiaId id = criarMciMicroorganismoPatologiaId(mciPatologiaInfeccao.getSeq());
		entidade.setMciPatologiaInfeccao(mciPatologiaInfeccao);
		entidade.setId(id);
		entidade.setRapServidores(usuario);
		entidade.setCriadoEm(new Date());
		mciMicroorganismoPatologiaDAO.persistir(entidade);
	}

	//I4 RN5
	public void inserirMciMicroorganismoPatologiaExame(
			MciMicroorganismoPatologia patologia,
			ResultadoCodificadoExameVO entidade, RapServidores usuario)
					throws ApplicationBusinessException {

		MciMicroorgPatologiaExameId id = new MciMicroorgPatologiaExameId(
				patologia.getId().getPaiSeq(), patologia.getId().getSeqp(), entidade.getRcdSeq(),
				entidade.getGtcSeq());
		MciMicroorgPatologiaExame exame = new MciMicroorgPatologiaExame();
		exame.setId(id);
		List<MciMicroorganismoPatologia> itens = mciMicroorganismoPatologiaDAO.buscarMicroorganismoAssociadoExame(id.getMptPaiSeq(), id.getMptSeqp(), id.getRcdSeqp(), id.getRcdGtcSeq());
		if(itens == null || itens.isEmpty()) {
			exame.setMciMicroorganismoPatologia(patologia);
			exame.setRapServidores(usuario);
			exame.setCriadoEm(new Date());
			exame.setIndSituacao(entidade.getSituacaoExame());
			AelResultadoCodificado aelResultadoCodificado = cadastroExamesFacade.obterAelResultadoCodificadoPorId(new AelResultadoCodificadoId(entidade.getGtcSeq(), entidade.getRcdSeq()));
			exame.setAelResultadoCodificado(aelResultadoCodificado);
			mciMicroorgPatologiaExameDAO.persistir(exame);
		} else {
			throw new  ApplicationBusinessException(MciMicroorganismoPatologiaRNException.AMP_MENSAGEM_REGISTRO_DUPLICADO);
		}
	}

	private MciMicroorganismoPatologiaId criarMciMicroorganismoPatologiaId(Integer seqPai) {
		Short seqP = mciMicroorganismoPatologiaDAO.obterProximoSeqPMciMicroorganismoPatologia(seqPai);
		return new MciMicroorganismoPatologiaId(seqPai, seqP);
	}


	// auxiliar de I1
	private MciMicroorganismoPatologiaJn criarMciMicroorganismoPatologiaJn(DominioOperacoesJournal operacao, MciMicroorganismoPatologia entidade, RapServidores usuarioLogado) {
		if(entidade.getRapServidoresMov() == null){
			return new MciMicroorganismoPatologiaJn(usuarioLogado.getUsuario(), operacao, entidade.getId().getPaiSeq(), entidade.getId().getSeqp(), entidade.getDescricao(),
					entidade.getIndSituacao(), entidade.getCriadoEm(), entidade.getRapServidores().getId().getMatricula(), entidade.getRapServidores().getId().getVinCodigo(),
					entidade.getAlteradoEm(), null, null);
		}
		return new MciMicroorganismoPatologiaJn(usuarioLogado.getUsuario(), operacao, entidade.getId().getPaiSeq(), entidade.getId().getSeqp(), entidade.getDescricao(),
				entidade.getIndSituacao(), entidade.getCriadoEm(), entidade.getRapServidores().getId().getMatricula(), entidade.getRapServidores().getId().getVinCodigo(),
				entidade.getAlteradoEm(), entidade.getRapServidoresMov().getId().getMatricula(),entidade.getRapServidoresMov().getId().getVinCodigo());

	}


	// auxiliar de I2
	private MciMicroorgPatologiaExameJn criarMciMicroorgPatologiaExameJn(DominioOperacoesJournal operacao, MciMicroorgPatologiaExame entidade, RapServidores usuarioLogado) {
		MciMicroorgPatologiaExameJn journal = null;
		if(entidade.getRapServidoresMov() == null) {
			journal = new MciMicroorgPatologiaExameJn(
					usuarioLogado.getUsuario(), operacao, entidade.getId()
					.getMptPaiSeq(), entidade.getId().getMptSeqp(),
					entidade.getIndSituacao(), entidade.getCriadoEm(), entidade
					.getRapServidores().getId().getMatricula(),
					entidade.getRapServidores().getId().getVinCodigo(),
					entidade.getAlteradoEm(), null, null);
		} else {
			journal = new MciMicroorgPatologiaExameJn(
					usuarioLogado.getUsuario(), operacao, entidade.getId()
					.getMptPaiSeq(), entidade.getId().getMptSeqp(),
					entidade.getIndSituacao(), entidade.getCriadoEm(), entidade
					.getRapServidores().getId().getMatricula(),
					entidade.getRapServidores().getId().getVinCodigo(),
					entidade.getAlteradoEm(), entidade.getRapServidoresMov()
					.getId().getMatricula(), entidade
					.getRapServidoresMov().getId().getVinCodigo());
		}
		journal.setRcdSeq(entidade.getId().getRcdSeqp());
		journal.setRcdGrupoSeq(entidade.getId().getRcdGtcSeq());
		return journal;
	}

	private void deletarExames(Set<MciMicroorgPatologiaExame> exames,
			RapServidores usuario) throws ApplicationBusinessException {
		for (MciMicroorgPatologiaExame item : exames) {
			deletarMicroorganismoPatologiaExame(item, usuario);
		}
	}


	// #1326 - D3
	private void deletarMciPatologiaExamePorId(MciMicroorgPatologiaExameId id) {
		MciMicroorgPatologiaExame entidade = mciMicroorgPatologiaExameDAO
				.obterPorChavePrimaria(id);
		mciMicroorgPatologiaExameDAO.remover(entidade);
	}

	private boolean isAlterandoDescricao(MciMicroorganismoPatologia entidade) {
		MciMicroorganismoPatologia original = mciMicroorganismoPatologiaDAO
				.obterPorChavePrimaria(entidade.getId());
		if (original == null) {
			return false;
		}
		return !(original.getDescricao().equals(entidade.getDescricao()));
	}

	// RN5
	private boolean isDentroPrazoExclusao(Date dataCriacao)
			throws ApplicationBusinessException {
		int diferenca = DateUtil
				.calcularDiasEntreDatas(dataCriacao, new Date());
		Integer limiteDiasExclusao = parametroFacade
				.buscarValorInteiro(AghuParametrosEnum.P_NRO_DIAS_PERM_DEL_MCI);
		if (limiteDiasExclusao == null) {
			return false;
		}
		if (diferenca > limiteDiasExclusao) {
			return false;
		}
		return true;
	}
}
