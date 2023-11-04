package lotto.controller;

import java.util.List;
import lotto.domain.dto.LottoDto;
import lotto.domain.mapper.LottoMapper;
import lotto.domain.model.Lotto;
import lotto.domain.model.LottoBonusNumber;
import lotto.domain.model.LottoGame;
import lotto.domain.model.LottoPurchaseCost;
import lotto.domain.model.LottoDispenser;
import lotto.domain.model.Lottos;
import lotto.domain.model.WinningLotto;
import lotto.domain.LottoRandomGenerator;
import lotto.domain.Repeater;
import lotto.view.InputView;
import lotto.view.OutputView;

public class LottoGameController {
    InputView inputView;
    OutputView outputView;
    Repeater repeater;

    public LottoGameController(InputView inputView, OutputView outputView, Repeater repeater) {
        this.inputView = inputView;
        this.outputView = outputView;
        this.repeater = repeater;
    }

    private LottoPurchaseCost readPurchaseCost() {
        outputView.printReadCostAmountMessage();

        return repeater.repeatBeforeSuccess(() -> new LottoPurchaseCost(inputView.readCostAmount()));
    }

    private Lottos buyLottos(int amount) {
        LottoDispenser lottoDispenser = new LottoDispenser(new LottoRandomGenerator());
        Lottos lottos = lottoDispenser.dispense(amount);
        outputView.printBuyingAmountMessage(amount);

        return lottos;
    }

    private LottoBonusNumber readBonusNumber() {
        outputView.printReadBonusNumberMessage();

        return repeater.repeatBeforeSuccess(() -> new LottoBonusNumber(inputView.readBonusNumber()));
    }

    private WinningLotto readLottoAnswer() {
        outputView.printReadWinningLottoMessage();
        Lotto winningLotto = repeater.repeatBeforeSuccess(() -> new Lotto(inputView.readWinningNumbers()));

        return repeater.repeatBeforeSuccess(() -> new WinningLotto(winningLotto, readBonusNumber()));
    }

    private void printBoughtLottos(Lottos lottos) {
        List<Lotto> rawLottos = lottos.getElements();
        List<LottoDto> lottoDtos = LottoMapper.toDtos(rawLottos);

        outputView.printLottos(lottoDtos);
    }

    private LottoGame initGame(LottoPurchaseCost lottoPurchaseCost) {
        int lottoAmount = lottoPurchaseCost.getLottoAmount();
        Lottos lottos = buyLottos(lottoAmount);
        printBoughtLottos(lottos);

        WinningLotto winningLotto = readLottoAnswer();

        return new LottoGame(lottos, winningLotto);
    }

    public void play() {
        LottoPurchaseCost lottoPurchaseCost = readPurchaseCost();
        LottoGame lottoGame = initGame(lottoPurchaseCost);
    }
}
